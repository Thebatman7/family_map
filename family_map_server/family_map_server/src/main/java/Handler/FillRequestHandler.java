package Handler;

import Service.Fill;
import Service.Request.FillRequest;
import Service.Result.FillResult;
import Service.Result.LoginResult;
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
The FillRequestHandler is the HTTP handler that processes incoming HTTP requests that contain
the "/fill/[username]/{generations}" URL path. Populates the server's database with generated data for
the specified user name. The required "username" parameter must be a user already registered with the server.
If there is any data in the database already associated with the given user name, it is deleted.
The optional “generations” parameter lets the caller specify the number of generations of ancestors to be generated,
and must be a non-negative integer (the default is 4, which results in 31 new persons each with associated events).
*/
public class FillRequestHandler implements HttpHandler {
    /*
    Since we are implementing the interface that means our handler classes can have this handle method that takes
    an instance of HTTPExchange.
    The FillRequestHandler is the HTTP handler that processes incoming HTTP requests that contain
    the "/fill/[username]/{generations}" URL path.
    */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            /*
            We determine the HTTP request type (GET, POST, etc.). Only allow POST requests for this operation.
            This operation requires a POST request, because the client is "posting" information to the server for processing.
            */
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                //we get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();//needed if AuthToken is required, not needed for fill
                //we get [username] and  {generations} in a string
                String requestString = exchange.getRequestURI().toString();
                //variables we will pass to the FillRequest object
                int generations = 4;
                String username = null;
                //splits string into segments
                String segments[] = requestString.split("/");
                //we grab username and generations
                if (segments.length == 4) {
                    username = segments[segments.length - 2];
                    generations = Integer.parseInt(segments[segments.length - 1]);
                }
                else if (segments.length == 3) {
                    username = segments[segments.length - 1];
                }
                FillRequest request = new FillRequest(username, generations);
                //we create a new fill service class object to pass the request object
                Fill fill = new Fill();
                //we create a result object to save what the fill method will return
                FillResult result = new FillResult();
                //we call the method to fill the data
                result = fill.fillData(request);
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
            else{
                //we expected a POST but got something else, so we return a "bad request status code to the client.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);//HTTP response status is 400
            }
            //We close the output stream. This is how Java knows we are done sending data and the response is complete
            exchange.getResponseBody().close();
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
