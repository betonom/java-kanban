package com.github.betonom.java_kanban.http.handlers;


import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.exceptions.HasInteractionException;
import com.github.betonom.java_kanban.exceptions.ManagerSaveException;
import com.github.betonom.java_kanban.exceptions.NotFoundException;
import com.github.betonom.java_kanban.managers.TaskManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();
        String response;

        switch (method) {
            case "GET" -> {
                if (pathParts[1].equals("subtask") && pathParts.length == 2) {
                    response = gson.toJson(taskManager.getSubtasksList());
                    sendOk(exchange, response);
                }

                if (pathParts[1].equals("subtasks") && pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);
                        Subtask subtask = taskManager.getSubtaskById(id);

                        response = gson.toJson(subtask);
                        sendOk(exchange, response);

                    } catch (NumberFormatException e) {

                        response = "Невозможно найти задачу: id должен быть числом";
                        sendBadRequest(exchange, response);

                    } catch (NotFoundException e) {

                        response = "Задача не найдена";
                        sendNotFound(exchange, response);

                    }
                }
            }

            case "POST" -> {
                if (pathParts[1].equals("subtasks") && pathParts.length == 2) {
                    try {
                        InputStream input = exchange.getRequestBody();
                        String request = new String(input.readAllBytes(), StandardCharsets.UTF_8);

                        if (request.isEmpty()) {
                            response = "Невозможно добавить подзадачу: пустое тело запроса";
                            sendHasInteraction(exchange, response);
                            return;
                        }

                        Subtask subtask = gson.fromJson(request, Subtask.class);

                        taskManager.createNewSubtask(subtask);

                        response = "Подзадача успешно добавлена!";
                        sendCreated(exchange, response);
                    } catch (HasInteractionException e) {

                        response = "Невозможно добавить подзадачу: пересечение с другими задачами";
                        sendHasInteraction(exchange, response);

                    } catch (ManagerSaveException e) {

                        response = "Ошибка сохранения. Повторите попытку позже";
                        sendInternalServerError(exchange, response);

                    }
                }

                if (pathParts[1].equals("subtasks") && pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);

                        InputStream input = exchange.getRequestBody();
                        String request = new String(input.readAllBytes(), StandardCharsets.UTF_8);

                        if (request.isEmpty()) {
                            response = "Невозможно обновить задачу: пустое тело запроса";
                            sendHasInteraction(exchange, response);
                            return;
                        }

                        Subtask subtask = gson.fromJson(request, Subtask.class);

                        subtask.setId(id);

                        taskManager.updateSubtask(subtask);

                        response = "Задача успешно обновлена!";
                        sendCreated(exchange, response);
                    } catch (HasInteractionException e) {

                        response = "Невозможно обновить задачу: пересечение с другими задачами";
                        sendHasInteraction(exchange, response);

                    } catch (NumberFormatException e) {

                        response = "Невозможно обновить задачу: id должен быть числом";
                        sendBadRequest(exchange, response);

                    } catch (NotFoundException e) {

                        response = "Невозможно обновить задачу: задача для обновления не найдена";
                        sendNotFound(exchange, response);

                    } catch (ManagerSaveException e) {

                        response = "Ошибка сохранения. Повторите попытку позже";
                        sendInternalServerError(exchange, response);

                    }
                }
            }

            case "DELETE" -> {
                if (pathParts[1].equals("subtasks") && pathParts.length == 2) {
                    try {
                        taskManager.clearSubtasks();
                        response = "Подзадачи успешно удалены!";
                        sendCreated(exchange, response);
                    } catch (ManagerSaveException e) {

                        response = "Ошибка сохранения. Повторите попытку позже";
                        sendInternalServerError(exchange, response);

                    }
                }

                if (pathParts[1].equals("subtasks") && pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);

                        taskManager.removeSubtaskById(id);

                        response = "Задача успешно удалена!";
                        sendCreated(exchange, response);

                    } catch (NumberFormatException e) {

                        response = "Невозможно найти задачу: id должен быть числом";
                        sendBadRequest(exchange, response);

                    } catch (NotFoundException e) {

                        response = "Задача не найдена";
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
