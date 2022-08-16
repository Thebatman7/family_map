package Handler;

import Service.PersonService;
import Service.Request.PersonRequest;
import Service.Result.SinglePersonResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

/*
The PersonRequestHandler is the HTTP handler that processes incoming HTTP requests that contain
the "/person/[personID]" URL path. Returns the single Person object with the specified ID.
*/
public class PersonRequestHandler implements HttpHandler {
    /*
    Since we are implementing the interface that means our handler classes can have this handle method that takes
    an instance of HTTPExchange.
    The PersonRequestHandler is the HTTP handler that processes incoming HTTP request that contain
    the "/person/[personID]" URL path.
    */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try{
            /*
            We determine the HTTP request type (GET, POST, etc.). Only allow GET requests for this operation.
            This operation requires a GET request, because the client is "getting" information from the server,
            and the operation is "ready only" (i.e., does not modify the state of the server).
            */
            if(exchange.getRequestMethod().toUpperCase().equals("GET")){
                //we get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();//needed if AuthToken is required, needed for this one
                //we extract the authToken from the "Authorization" header
                String authToken = reqHeaders.getFirst("Authorization");
                //we get the request URI in a string
                String requestString = exchange.getRequestURI().toString();
                //System.out.println(requestString);
                //splits string into segments
                String segments[] = requestString.split("/");
                //we grab personID
                String personID = segments[segments.length - 1];
                //we create a new person service class object to pass the request object
                PersonService personService = new PersonService();
                //we create a result object to save what the getSinglePerson method will return
                SinglePersonResult result = new SinglePersonResult();
                //we create a new request object
                PersonRequest request = new PersonRequest(personID, authToken);
                //we call the method to get the person
                result = personService.getSinglePerson(request);//FIX ME
                //changing the result object back into a Json string
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonString = gson.toJson(result);
                //we start sending the HTTP response to the client, starting with the status code and any defined headers.
                if (result.getSuccess()) {//if success is true we send HTTP_OK
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);//HTTP response status is 200
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);//HTTP response status is 400
                }
                /*
                Once the status code and headers have been sent to the client, we send the JSON data in
                the HTTP response body. We get the response body output stream.
                */
                OutputStream responseBody = exchange.getResponseBody();
                //we write the JSON string to the output stream.
                writeString(jsonString, responseBody);
                //we close the output stream. This is how Java knows we are done sending data and the response is complete
                responseBody.close();
            }
            else {
                //we expected a GET but got something else, so we return a "bad request" status code to the client.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);//HTTP response status is 400
            }
            /*
            We can close the output stream this way:
            exchange.getResponseBody().close();
            or the way we closed it up above:
            responseBody.close();
            for both Java knows we are done sending data and the response is complete.
            */
        }
        catch (IOException exception) {
            /*
            Some kind of internal error has occurred inside the server (not the client's fault), so we return
            an "internal server error" status code to the client.
            */
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            /*
            We are not sending a response body, so we close the response body output stream, indicating that the response is complete.
            */
            exchange.getRequestBody().close();
            exception.printStackTrace();
        }
    }

    //this method writes a String to an OutputStream.
    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write(str);
        bw.flush();
    }
}
