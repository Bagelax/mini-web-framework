package webserver;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String method;
    private String location;
    private HashMap<String, String> headers;
    private HashMap<String, String> queryArgs;
    private Map<?, ?> bodyArgs;

    public Request() {
        this("GET", "/");
    }

    public Request(String method, String location) {
        this(method, location, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public Request(String method,
                   String location,
                   HashMap<String, String> headers,
                   HashMap<String, String> queryArgs,
                   Map<?, ?> bodyArgs) {
        this.method = method;
        this.location = location;
        this.headers = headers;
        this.queryArgs = queryArgs;
        this.bodyArgs = bodyArgs;
    }

    public Map<?, ?> getBodyArgs() {
        return this.bodyArgs;
    }

    public String getMethod() {
        return this.method;
    }

    public String getLocation() {
        return this.location;
    }

    public HashMap<String, String> getHeader() {
        return this.headers;
    }
}
