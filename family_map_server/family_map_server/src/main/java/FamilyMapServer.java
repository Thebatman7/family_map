import DAO.Generator;
import Handler.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;


//Server Path: C:\Users\Batman\IdeaProjects\FamilyMapServerStudent-master\FamilyMapServerStudent-master\FamilyServer.db

public class FamilyMapServer {
    /*
    We have a main method so it can start up the program.
    This main method receives a port number as a command line parameter
    */
    public static void main(String[] args) {
        try {
            startServer(Integer.parseInt(args[0]));
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /*
    We will use this method to start up any instance of HTTP server
    */
    private static void startServer(int port) throws IOException {
        //this object represents a port
        InetSocketAddress serverAddress = new InetSocketAddress(port);
        /*
        We use the static create method on the HTTP server class.
        The two parameters passed are the InetSocketAddress instance and the size of a queue or a backlog of incoming requests.
        This means that this server can handle up to 10 requests at a time so if ten requests came in really close or
        simultaneously it would process one and the rest would go inside of the queue where it can request incoming
        requests. if we get 11 the 11th would get a  500 level error saying the server is busy and can't handle the request.
        */
        HttpServer server = HttpServer.create(serverAddress, 10);
        /*
        We need to establish the link between the handlers and the URLs. We will register different handler instances with
        the server where we specify which incoming URL should causes to go to each handler.
        */
        registerHandlers(server);
        /*
        Once we have registered our handlers we are ready to start up our server. This will cause the server to start
        listening on its port and from that point on any client that request that port will routed to the server and the
        server will handle that request.
        */
        server.start();
        System.out.println("FamilyMapServer listening on port " + port);
    }


    public static void registerHandlers(HttpServer server) {
        /*
        createContext method maps a URL to an instance of a handler.
        We call this method once for every handler we have. So, the server instance that we passed in knows that if it gets
        any requests that have any of the paths we have stated in the first parameter of the createContext method then it
        will pass those on to the respective register request handler by calling the classes handle methods.
        */
        server.createContext("/", new FileRequestHandler());//default
        server.createContext("/user/register", new RegisterRequestHandler());
        server.createContext("/user/login", new LoginRequestHandler());
        server.createContext("/clear", new ClearRequestHandler());
        server.createContext("/fill/", new FillRequestHandler());
        server.createContext("/load", new LoadRequestHandler());
        server.createContext("/person/", new PersonRequestHandler());//returns a person
        server.createContext("/person", new PersonsRequestHandler());//returns all people
        server.createContext("/event/", new EventRequestHandler());//returns an event
        server.createContext("/event", new EventsRequestHandler());//returns all events
    }

}
