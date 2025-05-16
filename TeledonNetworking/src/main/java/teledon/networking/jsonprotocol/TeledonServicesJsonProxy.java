package teledon.networking.jsonprotocol;

import com.google.gson.Gson;
import teledon.model.CharityCase;
import teledon.model.Donation;
import teledon.model.Donor;
import teledon.model.Volunteer;
import teledon.networking.dto.DonationDTO;
import teledon.services.ITeledonObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teledon.services.ITeledonServices;
import teledon.services.TeledonException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TeledonServicesJsonProxy implements ITeledonServices {
    private String host;
    private int port;

    private ITeledonObserver client;

    private BufferedReader input;
    private PrintWriter output;
    private Gson gsonFormatter;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    private static Logger logger = LogManager.getLogger(TeledonServicesJsonProxy.class);

    public TeledonServicesJsonProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
    }

    public Volunteer login(String username, String password, ITeledonObserver client) throws TeledonException {
        initializeConnection();
        Volunteer volunteer=new Volunteer(username,password);
        Request req= JsonProtocolUtils.createLoginRequest(volunteer);
        sendRequest(req);
        Response response=readResponse();
        if (response.getType()== ResponseType.LOGIN){
            this.client=client;
            return response.getVolunteer();
        }
        else{
            String err=response.getErrorMessage();;
            closeConnection();
            throw new TeledonException(err);
        }
    }

    @Override
    public List<CharityCase> findAllCases() throws TeledonException {
        Request req=JsonProtocolUtils.createGetAllCharityCasesRequest();
        logger.debug("Sending request {}",req);
        sendRequest(req);
        Response response=readResponse();
        if (response.getType()== ResponseType.ERROR){
            String err=response.getErrorMessage();//data().toString();
            throw new TeledonException(err);
        }
        List<CharityCase> charityCases= Arrays.stream(response.getCharityCases()).toList();
        return charityCases;
    }

    @Override
    public List<Donor> findAllDonors() throws TeledonException{
        Request req=JsonProtocolUtils.createGetAllDonorsRequest();
        sendRequest(req);
        Response response=readResponse();
        if (response.getType()== ResponseType.ERROR){
            String err=response.getErrorMessage();//data().toString();
            throw new TeledonException(err);
        }
        List<Donor> donors= Arrays.stream(response.getDonors()).toList();
        return donors;
    }

    @Override
    public void donate(String name, String number, String address, Integer sum, Integer idCharityCase) throws TeledonException {
        DonationDTO donation = new DonationDTO(name, number, address, sum, idCharityCase);
        Request req=JsonProtocolUtils.createDonateRequest(donation);
        sendRequest(req);
        Response response=readResponse();
        if (response.getType()== ResponseType.ERROR){
            String err=response.getErrorMessage();//data().toString();
            throw new TeledonException(err);
        }
    }

    public void logout(Volunteer volunteer, ITeledonObserver client) throws TeledonException {

        Request req=JsonProtocolUtils.createLogoutRequest(volunteer);
        sendRequest(req);
        Response response=readResponse();
        closeConnection();
        if (response.getType()== ResponseType.ERROR){
            String err=response.getErrorMessage();//data().toString();
            throw new TeledonException(err);
        }
    }

    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }

    }

    private void sendRequest(Request request)throws TeledonException {
        String reqLine=gsonFormatter.toJson(request);
        try {
            output.println(reqLine);
            output.flush();
        } catch (Exception e) {
            throw new TeledonException("Error sending object "+e);
        }

    }

    private Response readResponse() throws TeledonException {
        Response response=null;
        try{

            response=qresponses.take();

        } catch (InterruptedException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
        return response;
    }
    private void initializeConnection() throws TeledonException {
        try {
            gsonFormatter=new Gson();
            connection=new Socket(host,port);
            output=new PrintWriter(connection.getOutputStream());
            output.flush();
            input=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            finished=false;
            startReader();
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(Response response){
        if (response.getType() == ResponseType.UPDATE){
            CharityCase[] charityCases = response.getCharityCases();
            logger.debug("CharityCases received");
            try {
                client.donationsUpdated(Arrays.stream(charityCases).toList());
            } catch (TeledonException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
    }

    private boolean isUpdate(Response response) {
        // Verificăm dacă obiectul response este null pentru a evita excepția NullPointerException
        if (response == null) {
            logger.warn("Received null response in isUpdate check.");
            return false;
        }
        return response.getType() == ResponseType.UPDATE;
    }

    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // Citește răspunsul de la server
                    String responseLine = input.readLine();

                    if (responseLine == null || responseLine.trim().isEmpty()) {
                        // Afișează un mesaj de avertizare și eventual închide conexiunea
                        logger.warn("Received null or empty response line from server.");
                        break; // Ieșim din buclă pentru a evita alte operațiuni inutile
                    }

                    // Parsează răspunsul JSON
                    Response response = gsonFormatter.fromJson(responseLine, Response.class);

                    if (response.getType().equals("ERROR")) {
                        // Tratează eroarea - autentificare nereușită
                        logger.error("Error received from server: " + response.getErrorMessage());
                        throw new RuntimeException("Authentication failed: " + response.getErrorMessage());
                    }

                    // Continuă procesarea răspunsului dacă nu au existat erori
                    logger.debug("Response received: " + responseLine);
                    if (isUpdate(response)) {
                        handleUpdate(response);
                    } else {
                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage());
                            logger.error(e.getStackTrace());
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Connection error: " + e.getMessage(), e);
            } catch (RuntimeException e) {
                logger.error("Runtime exception: " + e.getMessage(), e);
            } finally {
                try {
                    // Închide resursele la finalizare
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                    logger.error("Failed to close the bufferedReader: " + e.getMessage());
                }
            }
        }
    }

}
