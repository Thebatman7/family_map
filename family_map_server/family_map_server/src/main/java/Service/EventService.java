package Service;

import DAO.AuthTokenDAO;
import DAO.DataAccessException;
import DAO.Database;
import DAO.EventDAO;
import Model.AuthToken;
import Model.Event;
import Service.Request.EventRequest;
import Service.Result.EventsResult;
import Service.Result.SingleEventResult;

import java.util.ArrayList;
import java.util.List;

public class EventService {
    /**
     * Gets a single Event object with the ID specified in the service.request.
     * @param request which is an EventRequest object containing eventID and authToken
     * @return a result object (SingleEventResult object if successful)
     */
    public SingleEventResult getSingleEvent(EventRequest request) {
        boolean commit = false;
        Database db = new Database();
        SingleEventResult result = new SingleEventResult();
        String eventID = request.getEventID();
        String authToken = request.getAuthToken();

        try{
            db.openConnection();//this whole thing db.openConnection() is the connection that openConnection returns
            EventDAO eDao = new EventDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

            Event foundEvent =  eDao.find(eventID);
            AuthToken foundToken = aDao.findWithToken(authToken);

            if (foundEvent == null) {
                result.setSuccess(false);
                result.setMessage("Error: Invalid eventID. Unable to fetch /event/" + eventID);
            }
            else if (foundToken == null) {
                result.setSuccess(false);
                result.setMessage("Error: Invalid AuhToken. Unable to fetch /event/" + authToken);
            }
            else {
                //we check whether userName associated with foundToken is the same as associatedUsername for found person
                if (foundEvent.getUsername().equals(foundToken.getUsername())) {
                    result.setSuccess(true);
                    result.setAssociatedUsername(foundToken.getUsername());
                    result.setPersonID(foundEvent.getPersonID());
                    result.setEventID(foundEvent.getEventID());
                    result.setLatitude(foundEvent.getLatitude());
                    result.setLongitude(foundEvent.getLongitude());
                    result.setCountry(foundEvent.getCountry());
                    result.setCity(foundEvent.getCity());
                    result.setEventType(foundEvent.getEventType());
                    result.setYear(foundEvent.getYear());
                    commit = true;
                }
                else {
                    result.setSuccess(false);
                    result.setMessage("Error: Requested event does not belong to provided authToken, " + authToken + ".");
                }
            }
            //we close connection
            db.closeConnection(commit);
        }
        catch (DataAccessException dbException) {
            dbException.printStackTrace();
        }
        return result;
    }

    /**
     * Gets all events for all family members of user
     * @param request which is an EventRequest object containing eventID and authToken
     * @return a result object (EventsResult object if successful)
     */
    public EventsResult getAllEvents(EventRequest request) {
        Boolean commit = false;
        Database db = new Database();
        EventsResult result = new EventsResult();
        String authToken = request.getAuthToken();

        try{
            db.getConnection();
            EventDAO eDAO = new EventDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());
            AuthToken authorization = aDao.findWithToken(authToken);
            if (authorization != null && authToken != null) {
                List<Event> eventsList = new ArrayList<>();
                eventsList = eDAO.findAllFamilyEvents(authorization.getUsername());
                if (eventsList != null && eventsList.size() != 0) {
                    //converting the List of events to an Array of events
                    Event[] eventsArray = new Event[eventsList.size()];
                    eventsArray = eventsList.toArray(eventsArray);
                    result.setData(eventsArray);
                    result.setSuccess(true);
                    commit = true;
                }
                else {
                    result.setSuccess(false);
                    result.setMessage("Error: There are not any events associated to the user in the Database.");
                }
            }
            else{
                result.setSuccess(false);
                result.setMessage("Error: The auth token is invalid.");
            }
            //we close connection
            db.closeConnection(commit);
        }
        catch(DataAccessException dbException) {
            dbException.printStackTrace();
        }
        return result;
    }
}
