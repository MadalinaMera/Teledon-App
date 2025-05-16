package teledon.networking.jsonprotocol;

import teledon.model.CharityCase;
import teledon.model.Donation;
import teledon.model.Donor;
import teledon.model.Volunteer;
import teledon.networking.dto.DonationDTO;
import teledon.services.ITeledonObserver;
import teledon.services.ITeledonServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teledon.services.TeledonException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.google.gson.Gson;

import static teledon.networking.jsonprotocol.JsonProtocolUtils.createLoginResponse;

public class TeledonClientJsonWorker implements Runnable, ITeledonObserver {
    private ITeledonServices server;
    private Socket connection;

    private BufferedReader input;
    private PrintWriter output;
    private Gson gsonFormatter;
    private volatile boolean connected;

    private static Logger logger = LogManager.getLogger(TeledonClientJsonWorker.class);

    public TeledonClientJsonWorker(ITeledonServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        gsonFormatter=new Gson();
        try{
            output=new PrintWriter(connection.getOutputStream());
            input=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            connected=true;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    public void run() {
        while(connected){
            try {
                String requestLine=input.readLine();
                Request request=gsonFormatter.fromJson(requestLine, Request.class);
                Response response=handleRequest(request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error("Error "+e);
        }
    }
    private static Response okResponse=JsonProtocolUtils.createOkResponse();

    private Response handleRequest(Request request){
        Response response=null;
        if (request.getType() == RequestType.LOGIN){
            logger.debug("Login request ...{}"+request.getVolunteer());
            Volunteer volunteer=request.getVolunteer();
            try {
                Volunteer volunteerRecv = server.login(volunteer.getUsername(),volunteer.getPassword(), this);
                return createLoginResponse(volunteerRecv);
            } catch (TeledonException e) {
                connected=false;
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }
        if (request.getType()== RequestType.LOGOUT){
            logger.debug("Logout request {}",request.getVolunteer());
            Volunteer volunteer = request.getVolunteer();
            try {
                server.logout(volunteer, this);
                connected=false;
                return okResponse;

            } catch (TeledonException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }
        if (request.getType()== RequestType.DONATE){

            DonationDTO donation=request.getDonation();
            logger.debug("DonateRequest ...{} ",donation);
            try {
                server.donate(donation.getName(), donation.getNumber(),
                        donation.getAddress(), donation.getSum(), donation.getIdCharityCase());
            } catch (TeledonException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
            return okResponse;
        }

        if (request.getType()== RequestType.GET_CHARTITY_CASES){
            logger.debug("GetCharityCases Request ...user= {}");
            try{
                List<CharityCase> casesList = server.findAllCases();
                CharityCase[] charityCases = (casesList != null) ? casesList.toArray(new CharityCase[0]) : new CharityCase[0];
                return JsonProtocolUtils.createGetAllCharityCasesResponse(charityCases);
            } catch (TeledonException e) {
            return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }
        if (request.getType()== RequestType.GET_DONORS){
            logger.debug("GetCharityCases Request ...user= {}");
            try {
                List<Donor> donors = server.findAllDonors();
                Donor[] donorsArray = (donors != null) ? donors.toArray(new Donor[0]) : new Donor[0];
                return JsonProtocolUtils.createGetAllDonorsResponse(donorsArray);
            }catch (TeledonException e){
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }
        return response;
    }

    private void sendResponse(Response response) throws IOException{
        String responseLine=gsonFormatter.toJson(response);
        logger.debug("sending response "+responseLine);
        synchronized (output) {
            output.println(responseLine);
            output.flush();
        }
    }

    @Override
    public void donationsUpdated(List<CharityCase> updatedCharityCases) throws TeledonException {
        CharityCase[] casesArray = updatedCharityCases.toArray(new CharityCase[0]);
        Response resp = JsonProtocolUtils.createUpdateResponse(casesArray);
        logger.debug("Updated charity cases received  ");
        try {
            sendResponse(resp);
        } catch (IOException e) {
            throw new TeledonException("Sending error: "+e);
        }
    }
}