package com.github.betonom.java_kanban.http.handlers;

import com.github.betonom.java_kanban.http.handlers.typeadapters.DurationAdapter;
import com.github.betonom.java_kanban.http.handlers.typeadapters.LocalDateTimeAdapter;
import com.github.betonom.java_kanban.managers.TaskManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    protected final Gson gson;
    protected final TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        this.taskManager = taskManager;
    }
    protected void sendOk(HttpExchange exchange, String responseString) throws IOException {
        byte[] response = responseString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(200, response.length);
            os.write(response);
        }
    }

    protected void sendCreated(HttpExchange exchange, String responseString) throws IOException {
        byte[] response = responseString.getBytes(StandardCharsets.UTF_8);

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(201, response.length);
            os.write(response);
        }
    }

    protected void sendBadRequest(HttpExchange exchange, String responseString) throws IOException{
        byte[] response = responseString.getBytes(StandardCharsets.UTF_8);

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(400, response.length);
            os.write(response);
        }
    }

    protected void sendNotFound(HttpExchange exchange, String responseString) throws IOException {
        byte[] response = responseString.getBytes(StandardCharsets.UTF_8);

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(404, response.length);
            os.write(response);
        }
    }

    protected void sendHasInteraction(HttpExchange exchange, String responseString) throws IOException {
        byte[] response = responseString.getBytes(StandardCharsets.UTF_8);

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(406, response.length);
            os.write(response);
        }
    }

    protected void sendInternalServerError(HttpExchange exchange, String responseString) throws IOException {
        byte[] response = responseString.getBytes(StandardCharsets.UTF_8);

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(500, response.length);
            os.write(response);
        }
    }
}