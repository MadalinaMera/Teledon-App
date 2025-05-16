package teledon.networking.jsonprotocol;


import teledon.model.CharityCase;
import teledon.model.Donation;
import teledon.model.Donor;
import teledon.model.Volunteer;
import teledon.networking.dto.DonationDTO;

public class JsonProtocolUtils {
    public static Response createGetAllCharityCasesResponse(CharityCase[] C){
        Response resp=new Response();
        resp.setType(ResponseType.GET_ALL_CHARITY_CASES);
        resp.setCharityCases(C);
        return resp;
    }

    public static Response createGetAllDonorsResponse(Donor[] donors){
        Response resp=new Response();
        resp.setType(ResponseType.GET_ALL_DONORS);
        resp.setDonors(donors);
        return resp;
    }

    public static Response createOkResponse(){
        Response resp=new Response();
        resp.setType(ResponseType.OK);
        return resp;
    }

    public static Response createErrorResponse(String errorMessage){
        Response resp=new Response();
        resp.setType(ResponseType.ERROR);
        resp.setErrorMessage(errorMessage);
        return resp;
    }

    public static Response createUpdateResponse(CharityCase[] cases){
        Response resp=new Response();
        resp.setType(ResponseType.UPDATE);
        resp.setCharityCases(cases);
        return resp;
    }

    public static Request createLoginRequest(Volunteer volunteer){
        Request req=new Request();
        req.setType(RequestType.LOGIN);
        req.setVolunteer(volunteer);
        return req;
    }

    public static Request createDonateRequest(DonationDTO donation){
        Request req=new Request();
        req.setType(RequestType.DONATE);
        req.setDonation(donation);
        return req;
    }

    public static Request createLogoutRequest(Volunteer volunteer){
        Request req=new Request();
        req.setType(RequestType.LOGOUT);
        req.setVolunteer(volunteer);
        return req;
    }

    public static Request createGetAllDonorsRequest(){
        Request req=new Request();
        req.setType(RequestType.GET_DONORS);
        return req;
    }
    public static Request createGetAllCharityCasesRequest(){
        Request req=new Request();
        req.setType(RequestType.GET_CHARTITY_CASES);
        return req;
    }

    public static Response createLoginResponse(Volunteer volunteer) {
        Response response = new Response();
        response.setType(ResponseType.LOGIN);
        response.setVolunteer(volunteer);
        return response;
    }

}
