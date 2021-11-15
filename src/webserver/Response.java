package webserver;

import com.google.gson.Gson;

import java.util.HashMap;

public class Response {
    private HashMap<String, String> headers;
    private Object body;
    private Integer status;
    private Gson parser = new Gson();

    public Response() {
        this(new HashMap<>(), null, 200);
    }

    public Response(Object body) {
        this(new HashMap<>(), body, 200);
    }

    public Response(Object body, Integer status) {
        this(new HashMap<>(), body, status);
    }

    public Response(HashMap<String, String> headers, Object body) {
        this.headers = headers;
        this.body = body;
        this.status = 200;
    }

    public Response(HashMap<String, String> headers, Object body, Integer status) {
        this.headers = headers;
        this.body = body;
        this.status = status;
    }

    public String render() {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 " + this.status + " " + this.getResponseText() + "\n");
        this.headers.forEach((key, value) -> {
            response.append(key).append(":").append(value).append("\n");
        });
        response.append("\n");
        if (this.body != null)
            response.append(this.parser.toJson(this.body));
        return response.toString();
    }

    private String getResponseText() {
        if (this.status < 200)
            return "Information response";
        if (this.status < 300)
            return "OK";
        if (this.status < 400)
            return "Redirect message";
        if (this.status < 500)
            return "Client error";
        if (this.status < 600)
            return "Server error";
        return "";
    }
}
