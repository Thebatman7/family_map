package Encoder;

import com.google.gson.Gson;

public class JsonSerializer {

    /*
    We are using a generic. A generic allows us to specify a placeholder for a data type.
    We specify a generic type, <T>, so whatever class we specify in the parameter, Class<T> is the return type.
    A generic Gson deserialization method that will return a runtime specified data type. This will take a string
    and return an object.
    */
    public static <T> T deserialize(String value, Class<T> returnType) {
        return (new Gson()).fromJson(value, returnType);
    }

}
