package co.edu.escuelaing.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import co.edu.escuelaing.annotations.RequestParam;

public class HttpServer {
    private final int port;
    private final Map<String, Method> routes = new HashMap<>();
    private final Map<String, Object> controllers = new HashMap<>();

    public HttpServer(int port) { this.port = port; }

    public void registerRoute(String path, Method method, Object instance) {
        routes.put(path, method);
        controllers.put(path, instance);
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server running on http://localhost:" + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleRequest(clientSocket);
        }
    }

    private void handleRequest(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream());

        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            client.close();
            return;
        }

        String[] parts = requestLine.split(" ");
        String fullPath = parts[1];

        String path = fullPath.contains("?") ? fullPath.split("\\?")[0] : fullPath;
        String query = fullPath.contains("?") ? fullPath.split("\\?")[1] : "";

        if (routes.containsKey(path)) {
            Method method = routes.get(path);
            Object instance = controllers.get(path);
            try {
                String response = invokeMethod(method, instance, query);
                sendResponse(out, 200, response);
            } catch (Exception e) {
                sendResponse(out, 500, "Error: " + e.getMessage());
            }
        } else {
            sendResponse(out, 404, "<h1>404 - Not Found</h1>");
        }
        client.close();
    }

    private String invokeMethod(Method method, Object instance, String query)
            throws Exception {
        Map<String, String> params = parseQuery(query);
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            RequestParam rp = parameters[i].getAnnotation(RequestParam.class);
            if (rp != null) {
                args[i] = params.getOrDefault(rp.value(), rp.defaultValue());
            }
        }
        return (String) method.invoke(instance, args);
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }

    private void sendResponse(PrintWriter out, int code, String body) {
        out.println("HTTP/1.1 " + code + " OK");
        out.println("Content-Type: text/html");
        out.println("Connection: close");
        out.println();
        out.println(body);
        out.flush();
    }
}