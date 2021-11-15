package helpers;

import java.util.HashMap;

public class RequestHelper {
    public static HashMap<String, String> getQueryArgs(String url) {
        String[] splitRoute = url.split("\\?");
        if(splitRoute.length == 1) {
            return new HashMap<>();
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        String[] pairs = splitRoute[1].split("&");
        for (String pair:pairs) {
            String[] keyPair = pair.split("=");
            parameters.put(keyPair[0], keyPair[1]);
        }

        return parameters;
    }
}
