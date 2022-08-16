package Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.util.Locale;


/*
This is the default handler. If we get a request that we don't have aa specific handler for we will end up here in the
default handler. So, all requests that are not specifically registered with a specific path will come here and we will
assume that they are looking for a file. When the HttServer object (declared in the FamilyMapServer class) receives a request
containing the "/" URL path, it calls FileRequestHandler.handle() which actually process the request.
*/
public class FileRequestHandler implements HttpHandler {
    /*
    Since we are implementing the interface that means our handler classes can have this handle method that takes
    an instance of HTTPExchange
    This handles HTTP requests containing the "/games/list" URL path. The "exchange" parameter is an HttpExchange object,
    which is defined by Java. In this context, an "exchange" is an HTTP request/response pair (i.e., the client and
    server exchange a request and response). The HttpExchange object gives the handler access to all of the
    details of the HTTP request (Request type [GET or POST], request headers, request body, etc.).
    The HttpExchange object also gives the handler the ability to construct an HTTP response and send it back to the
    client (Status code, headers, response body, etc.).
    */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            /*
            We determine the HTTP request type (GET, POST, etc.). Only allow GET requests for this operation.
            This operation requires a GET request, because the client is "getting" information from the server, and
            the operation is "read only" (i.e., does not modify the state of the server).
            */
            if (exchange.getRequestMethod().toUpperCase().equals("GET")) {
                //we will get the path part of the URL which will identify the file they are looking for
                String urlPath = exchange.getRequestURI().toString();
                System.out.println(urlPath);
                if (urlPath == null || urlPath.equals("/")) {
                    urlPath = "/index.html";//so we can send the file inside the web package of our project
                }
                //we append urlPath to a relative path to the directory containing the files
                String filePath = "web" + urlPath;//this should be a path to where the actual file is
                File file = new File(filePath);
                if (file.exists()) {
                    /*
                    We start sending the HTTP response to the client. We send the status code and any defined headers.
                    the number 0 represent the response size, we use zero because the size of the response has to be precise which
                    can be hard to calculate, but if we use zero the programs gets the size automatically.
                    */
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                else {
                    /*
                    We start sending the HTTP response to the client. We send the status code and any defined headers.
                    the number 0 represent the response size, we use zero because the size of the response has to be precise which
                    can be hard to calculate, but if we use zero the programs gets the size automatically.
                    */
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);//HTTP response status is 404
                    filePath = "web/HTML/404.html";
                    file = new File(filePath);
                }
                //we send the JSON data in the HTTP response body
                OutputStream respBody = exchange.getResponseBody();
                /*
                We write the the JSON string to the output stream.
                We use the static copy method on the file we created.
                */
                Files.copy(file.toPath(), respBody);
                /*
                This closes and sends the response body to the client. This is how Java knows we are done sending data.
                We either have to close the response body or the exchange.
                If we close the exchange it will close the response body as well.
                */
                respBody.close();
            }
            else {
                //We expected a GET but got something else, we return a "bad request"
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);//HTTP response status is 400
            }
        }
        catch(IOException exception) {
            /*
            Some kind of internal error has occurred inside the server (not the client's fault),
            so we return an "internal server error" status code to the client.
            */
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            /*
            Since the server is unable to complete the request, the client will not receive the inf., so we close
            the response body output stream, indicating that the response is complete.
             */
            exchange.getResponseBody().close();
            //we display the stack trace for inf. about the exception
            exception.printStackTrace();
        }
    }
}
