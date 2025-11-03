package com.github.betonom.java_kanban.http.handlers;


import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.exceptions.ManagerSaveException;
import com.github.betonom.java_kanban.exceptions.NotFoundException;
import com.github.betonom.java_kanban.managers.TaskManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();
        String response;

        switch (method) {
            case "GET" -> {
                if (pathParts[1].equals("epics") && pathParts.length == 2) {
                    response = gson.toJson(taskManager.getEpicsList());
                    sendOk(exchange, response);
                }

                if (pathParts[1].equals("epics") && pathParts.length == 3) {
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

                if (pathParts[1].equals("epics") && pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);

                        response = gson.toJson(taskManager.getEpicSubtasks(id));

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

            case "POST" -> {
                if (pathParts[1].equals("epics") && pathParts.length == 2) {
                    try {
                        String request = getStringRequest(exchange);

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

                if (pathParts[1].equals("epics") && pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);

                        String request = getStringRequest(exchange);

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
            }

            case "DELETE" -> {
                if (pathParts[1].equals("epics") && pathParts.length == 2) {
                    try {
                        taskManager.clearEpics();
                        response = "Эпики успешно удалены!";
                        sendCreated(exchange, response);
                    } catch (ManagerSaveException e) {

                        response = "Ошибка сохранения. Повторите попытку позже";
                        sendInternalServerError(exchange, response);

                    }
                }

                if (pathParts[1].equals("epics") && pathParts.length == 3) {
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
    }
}
