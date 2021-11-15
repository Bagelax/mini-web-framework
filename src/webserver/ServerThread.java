package webserver;

import com.google.gson.Gson;
import exceptions.RequestNotValidException;
import helpers.MapKey;
import helpers.MapVal;
import helpers.RequestHelper;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable {
    Socket socket;
    HashMap<MapKey, MapVal> map;
    private BufferedReader in;
    private PrintWriter out;
    Gson parser;

    public ServerThread(Socket socket, HashMap<MapKey, MapVal> map) {
        this.socket = socket;
        this.map = map;
        this.parser = new Gson();
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Request request = null;
        try {
            request = this.generateRequest();
            if(request == null) {
                in.close();
                out.close();
                socket.close();
                return;
            }

            MapVal handler = this.map.get(new MapKey(request.getLocation(), request.getMethod()));
            Response response;
            if (handler == null) {
                response = new Response("Not found", 404);
            }
            else {
                response = (Response) handler.invoke(request);
            }
            out.println(response.render());

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RequestNotValidException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    private Request generateRequest() throws IOException, RequestNotValidException {
        String command = in.readLine();
        if(command == null) {
            return null;
        }

        try {
            String[] actionRow = command.split(" ");
            String method = actionRow[0];
            String route = actionRow[1];
            HashMap<String, String> queryArgs = RequestHelper.getQueryArgs(route);
            HashMap<String, String> headers = new HashMap<>();
            Map<?, ?> bodyArgs = new HashMap<>();

            do {
                command = in.readLine();
                String[] headerRow = command.split(": ");
                if(headerRow.length == 2) {
                    headers.put(headerRow[0], headerRow[1]);
                }
            } while(!command.trim().equals(""));

            if(method.equals("POST")) {
                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                char[] buff = new char[contentLength];
                in.read(buff, 0, contentLength);
                String parametersString = new String(buff);

                bodyArgs = parser.fromJson(parametersString, Map.class);
            }

            return new Request(method, route, headers, queryArgs, bodyArgs);

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RequestNotValidException(command);
        }
    }
}
