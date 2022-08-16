package Handler;

import Encoder.JsonSerializer;
import Service.Load;
import Service.Request.LoadRequest;
import Service.Result.LoadResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;

/*
The LoadRequestHandler is the HTTP handler that processes incoming HTTP requests that contain the "/load" URL path.
Clears all data from the database (just like the /clear API), and then loads the posted user, person, and event data
into the database.
*/
public class LoadRequestHandler implements HttpHandler {
    /*
    Since we are implementing the interface that means our handler classes can have this handle method that takes
    an instance of HTTPExchange
    The LoadRequestHandler is the HTTP handler that processes incoming HTTP request that contain the "/load" URL path.
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
                Headers reqHeaders = exchange.getRequestHeaders();//needed if AuthToken is required, not needed for load
                //we extract the JSON string from the HTTP request body input stream
                InputStream reqBody = exchange.getRequestBody();
                //we read JSON string from the input stream, reqBody
                String reqData = readString(reqBody);
                //converting the String into an object
                LoadRequest request = JsonSerializer.deserialize(reqData, LoadRequest.class);
                //we create a new Load service class object to pass the request object
                Load load = new Load();
                //we create a result object to save what the load method will return
                LoadResult result = new LoadResult();
                //we call the method to load
                result = load.loadData(request);
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
                //we expected a POST but got something else, so we return a "bad request" status code to the client.
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

    //this method reads a String from a InputStream
    private String readString (InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        char[] buffer = new char[1024];
        int length;
        while ((length = streamReader.read(buffer)) > 0) {
            stringBuilder.append(buffer, 0, length);
        }
        return stringBuilder.toString();
    }

    //this method writes a String to an OutputStream.
    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write(str);
        bw.flush();
    }
}
