package teledon.networking.objectProtocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teledon.model.CharityCase;
import teledon.model.Donor;
import teledon.model.Volunteer;
import teledon.services.ITeledonObserver;
import teledon.services.ITeledonServices;
import teledon.services.TeledonException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class TeledonClientObjectWorker implements Runnable, ITeledonObserver {
    private ITeledonServices server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    private static Logger logger = LogManager.getLogger(TeledonClientObjectWorker.class);

    public TeledonClientObjectWorker(ITeledonServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            System.err.println("Error initializing streams: " + e.getMessage());
        }
    }

    public void run() {
        while(connected){
            try{
                Object request = input.readObject();
                Object response = handleRequest((Request) request);
                if(response != null){
                    sendResponse((Response) response);
                }
            }catch (IOException e){
                e.printStackTrace();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
            try{
                Thread.sleep(100);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        try{
            input.close();
            output.close();
            connection.close();
        }catch (IOException e){
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    private Response handleRequest(Request request){
        Response response=null;
        if (request instanceof LoginRequest){
            logger.debug("Login request ...");
            LoginRequest logReq=(LoginRequest)request;
            Volunteer volunteer =logReq.getVolunteer();
            try {
                server.login(volunteer.getUsername(), volunteer.getPassword(), this);
                return new OkResponse();
            } catch (TeledonException e) {
                connected=false;
                return new ErrorResponse(e.getMessage());
            }
        }
        if (request instanceof LogoutRequest){
            logger.debug("Logout request");
            LogoutRequest logReq=(LogoutRequest)request;
            Volunteer volunteer =logReq.getVolunteer();
            try {
                server.logout(volunteer, this);
                connected=false;
                return new OkResponse();

            } catch (TeledonException e) {
                return new ErrorResponse(e.getMessage());
            }
        }
        // Handle other request types here
        if(request instanceof CharityCasesUpdatedRequest){

        }
        return response;
    }
    private void sendResponse(Response response) throws IOException{
        logger.debug("sending response {}",response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }
    public void donorsUpdated(Iterable<Donor> updatedDonors) throws TeledonException {
        try {
            sendResponse(new DonorsUpdatedResponse(updatedDonors));
        } catch (IOException e) {
            logger.error("Error sending donors updated notification: {}", e.getMessage());
            throw new TeledonException("Error sending donors updated notification: " + e.getMessage());
        }
    }

    @Override
    public void donationsUpdated(List<CharityCase> updatedCharityCases) throws TeledonException {
        try {
            sendResponse(new CharityCasesUpdatedResponse(updatedCharityCases));
        } catch (IOException e) {
            logger.error("Error sending donations updated notification: {}", e.getMessage());
            throw new TeledonException("Error sending donations updated notification: " + e.getMessage());
        }
    }
}
