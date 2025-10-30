package com.github.betonom.java_kanban.http.handlers;

import com.github.betonom.java_kanban.managers.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();
        String response;

        if (pathParts[1].equals("prioritized") && pathParts.length == 2) {
            if (method.equals("GET")) {
                response = gson.toJson(taskManager.getPrioritizedTasks());
                sendOk(exchange, response);
            }
        }
    }
}
