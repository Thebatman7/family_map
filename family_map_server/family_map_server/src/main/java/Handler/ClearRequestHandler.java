package Handler;

import DAO.DataAccessException;
import Service.Clear;
import Service.Result.ClearResult;
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
The ClearRequestHandler is the HTTP handler that processes incoming HTTP requests that contain
the "/clear" URL path. Deletes ALL data from the database, including user accounts, auth tokens, and generated person and event data.
*/
public class ClearRequestHandler implements HttpHandler {
    /*
    Since we are implementing the interface that means our handler classes can have this handle method that takes
    an instance of HTTPExchange.
    The ClearRequestHandler is the HTTP handler that processes incoming HTTP requests that contain
    the "/clear" URL path.
    */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try{
            /*
            We determine the HTTP request type (GET, POST, etc.). Only allow POST requests for this operation.
            This operation requires a POST request, because the client is "posting" information to the server for processing.
            */
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                //we get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();//needed if we require an AuthToken, not needed for clear
                /*
                We usually extract the request body, but since Clear Service doesn't requite a request object we can
                just call the clear service class. However, we do need a ClearResult object.
                */
                Clear clear = new Clear();
                ClearResult result = new ClearResult();
                result = clear.deleteAll();
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
            }
            else {
                //we expected a POST but got something else, so we return a "bad request status code to the client.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);//HTTP response status is 400
            }
            //we are not sending a response body, we close the response body output stream, indicating that the response is complete.
            exchange.getResponseBody().close();
        }
        catch (IOException | DataAccessException exception) {
            /*
            Some kind of internal error has occurred inside the server (not the client's fault), so we return
            an "internal server error" status code to the client.
            */
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
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
