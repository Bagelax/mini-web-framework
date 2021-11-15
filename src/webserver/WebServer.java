package webserver;

import annotations.*;
import exceptions.DependencyInjectionException;
import exceptions.HandlerInvalidException;
import helpers.MapKey;
import helpers.MapVal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WebServer {


    private HashMap<MapKey, MapVal> routeMap = new HashMap<>();
    private DIEngine diEngine;

    private void discoverRoutes() throws HandlerInvalidException, DependencyInjectionException {
        Package[] packages = ClassLoader.getSystemClassLoader().getDefinedPackages();
        for (Package p : packages) {
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(p.getName());
            if (stream == null) continue;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            List<Class> classes = reader.lines()
                    .filter(line -> line.endsWith(".class"))
                    .map(line -> this.getClass(line, p.getName()))
                    .collect(Collectors.toList());
            for (Class c : classes) {
                if (c.isAnnotationPresent(Qualifier.class)) {
                    Qualifier q = (Qualifier) c.getAnnotation(Qualifier.class);
                    this.diEngine.register(q.value(), c);
                }
            }
            for (Class c : classes) {
                if (c.isAnnotationPresent(Controller.class)) {
                    try {
                        Object controllerInstance = c.getDeclaredConstructor().newInstance();
                        this.diEngine.initializeFields(controllerInstance);
                        for (Method m : c.getDeclaredMethods()) {
                            if ((m.isAnnotationPresent(GET.class) || m.isAnnotationPresent(POST.class)) &&
                                m.isAnnotationPresent(Path.class)) {
                                if (m.isAnnotationPresent(GET.class)) {
                                    String method = "GET";
                                    Path path = m.getAnnotation(Path.class);
                                    System.out.println("Added " + method + " on route " + path.value());
                                    if (m.getParameters()[0].getType() != Request.class)
                                        throw new HandlerInvalidException("first parameter must be webserver.Request.");
                                    if (m.getReturnType() != Response.class)
                                        throw new HandlerInvalidException("response must be webserver.Response.");
                                    MapKey key = new MapKey(path.value(), method);
                                    if (this.routeMap.containsKey(key))
                                        throw new HandlerInvalidException("Duplicate handler for " + key);
                                    this.routeMap.put(key, new MapVal(controllerInstance, m));

                                }
                                if (m.isAnnotationPresent(POST.class)) {
                                    String method = "POST";
                                    Path path = m.getAnnotation(Path.class);
                                    System.out.println("Added " + method + " on route " + path.value());
                                    if (m.getParameters()[0].getType() != Request.class)
                                        throw new HandlerInvalidException("first parameter must be webserver.Request.");
                                    if (m.getReturnType() != Response.class)
                                        throw new HandlerInvalidException("response must be webserver.Response.");
                                    MapKey key = new MapKey(path.value(), method);
                                    if (this.routeMap.containsKey(key))
                                        throw new HandlerInvalidException("Duplicate handler for " + key);
                                    this.routeMap.put(key, new MapVal(controllerInstance, m));
                                }
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void run(Integer port) {
        try {
            this.diEngine = new DIEngine();
            this.discoverRoutes();
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is running at http://localhost:" + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket, this.routeMap)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
