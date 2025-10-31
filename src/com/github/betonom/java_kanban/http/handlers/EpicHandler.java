package com.github.betonom.java_kanban.http.handlers;


import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.exceptions.ManagerSaveException;
import com.github.betonom.java_kanban.exceptions.NotFoundException;
import com.github.betonom.java_kanban.managers.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();
        String response;

        if (pathParts[1].equals("epics") && pathParts.length == 2) {
            switch (method) {
                case "GET" -> {
                    response = gson.toJson(taskManager.getEpicsList());
                    sendOk(exchange, response);
                }
                case "POST" -> {
                    try {
                        InputStream input = exchange.getRequestBody();
                        String request = new String(input.readAllBytes(), StandardCharsets.UTF_8);

                        if (request.isEmpty()) {
                            response = "Невозможно добавить эпик: пустое тело запроса";
                            sendHasInteraction(exchange, response);
                            return;
                        }

                        Epic epic = gson.fromJson(request, Epic.class);

                        taskManager.createNewEpic(epic);

                        response = "Эпик успешно добавлен!";
                        sendCreated(exchange, response);
                    } catch (ManagerSaveException e) {

                        response = "Ошибка сохранения. Повторите попытку позже";
                        sendInternalServerError(exchange, response);

                    }
                }
                case "DELETE" -> {
                    try {
                        taskManager.clearEpics();
                        response = "Эпики успешно удалены!";
                        sendCreated(exchange, response);
                    } catch (ManagerSaveException e) {

                        response = "Ошибка сохранения. Повторите попытку позже";
                        sendInternalServerError(exchange, response);

                    }
                }
            }
        }
        if (pathParts[1].equals("epics") && pathParts.length == 3) {
            switch (method) {
                case "GET" -> {
                    try {
                        int id = Integer.parseInt(pathParts[2]);
                        Epic epic = taskManager.getEpicById(id);

                        response = gson.toJson(epic);
                        sendOk(exchange, response);

                    } catch (NumberFormatException e) {

                        response = "Невозможно найти эпик: id должен быть числом";
                        sendBadRequest(exchange, response);

                    } catch (NotFoundException e) {

                        response = "Эпик не найдена";
                        sendNotFound(exchange, response);

                    }
                }
                case "POST" -> {
                    try {
                        int id = Integer.parseInt(pathParts[2]);

                        InputStream input = exchange.getRequestBody();
                        String request = new String(input.readAllBytes(), StandardCharsets.UTF_8);

                        if (request.isEmpty()) {
                            response = "Невозможно обновить эпик: пустое тело запроса";
                            sendHasInteraction(exchange, response);
                            return;
                        }

                        Epic epic = gson.fromJson(request, Epic.class);

                        epic.setId(id);

                        taskManager.updateEpic(epic);

                        response = "Эпик успешно обновлен!";
                        sendCreated(exchange, response);
                    } catch (NumberFormatException e) {

                        response = "Невозможно обновить эпик: id должен быть числом";
                        sendBadRequest(exchange, response);

                    } catch (NotFoundException e) {

                        response = "Невозможно обновить эпик: эпик для обновления не найдена";
                        sendNotFound(exchange, response);

                    } catch (ManagerSaveException e) {

                        response = "Ошибка сохранения. Повторите попытку позже";
                        sendInternalServerError(exchange, response);

                    }
                }
                case "DELETE" -> {
                    try {
                        int id = Integer.parseInt(pathParts[2]);

                        taskManager.removeEpicById(id);

                        response = "Эпик успешно удален!";
                        sendCreated(exchange, response);

                    } catch (NumberFormatException e) {

                        response = "Невозможно найти эпик: id должен быть числом";
                        sendBadRequest(exchange, response);

                    } catch (NotFoundException e) {

                        response = "Эпик не найден";
                        sendNotFound(exchange, response);

                    } catch (ManagerSaveException e) {

                        response = "Ошибка сохранения. Повторите попытку позже";
                        sendInternalServerError(exchange, response);

                    }
                }
            }
        }
        if (pathParts[1].equals("epics") && pathParts.length == 4 && pathParts[3].equals("subtasks")) {
            if (method.equals("GET")) {
                try {
                    int id = Integer.parseInt(pathParts[2]);

                    Epic epic = taskManager.getEpicById(id);

                    response = gson.toJson(taskManager.getEpicSubtasks(epic));

                    sendOk(exchange, response);

                } catch (NumberFormatException e) {

                    response = "Невозможно найти эпик: id должен быть числом";
                    sendBadRequest(exchange, response);

                } catch (NotFoundException e) {

                    response = "Эпик не найден";
                    sendNotFound(exchange, response);

                }
            }
        }
    }
}
