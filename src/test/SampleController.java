package test;

import annotations.*;
import webserver.Request;
import webserver.Response;

@Controller
public class SampleController {
    @GET
    @POST
    @Path("/health")
    public Response get(Request r) {
        return new Response("Ok");
    }

    @POST
    @Path("/echo")
    public Response post(Request r) {
        return new Response();
    }

    @Autowired
    @Qualifier("two")
    public InterfaceTwo i;
}
