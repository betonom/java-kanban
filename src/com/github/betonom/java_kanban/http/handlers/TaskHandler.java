package com.github.betonom.java_kanban.http.handlers;


import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.exceptions.HasInteractionException;
import com.github.betonom.java_kanban.exceptions.ManagerSaveException;
import com.github.betonom.java_kanban.exceptions.NotFoundException;
import com.github.betonom.java_kanban.managers.TaskManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();
        String response;

        switch (method) {
            case "GET" -> {
                if (pathParts[1].equals("tasks") && pathParts.length == 2) {
                    response = gson.toJson(taskManager.getTasksList());
                    sendOk(exchange, response);
                }

                if (pathParts[1].equals("tasks") && pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);
                        Task task = taskManager.getTaskById(id);

                        response = gson.toJson(task);
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
                if (pathParts[1].equals("tasks") && pathParts.length == 2) {
                    try {
                        InputStream input = exchange.getRequestBody();
                        String request = new String(input.readAllBytes(), StandardCharsets.UTF_8);

                        if (request.isEmpty()) {
                            response = "Невозможно добавить задачу: пустое тело запроса";
                            sendHasInteraction(exchange, response);
                            return;
                        }

                        Task task = gson.fromJson(request, Task.class);

                        taskManager.createNewTask(task);

                        response = "Задача успешно добавлена!";
                        sendCreated(exchange, response);
                    } catch (HasInteractionException e) {

                        response = "Невозможно добавить задачу: пересечение с другими задачами";
                        sendHasInteraction(exchange, response);

                    } catch (ManagerSaveException e) {

                        response = "Ошибка сохранения. Повторите попытку позже";
                        sendInternalServerError(exchange, response);

                    }
                }

                if (pathParts[1].equals("tasks") && pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);

                        InputStream input = exchange.getRequestBody();
                        String request = new String(input.readAllBytes(), StandardCharsets.UTF_8);

                        if (request.isEmpty()) {
                            response = "Невозможно обновить задачу: пустое тело запроса";
                            sendHasInteraction(exchange, response);
                            return;
                        }

                        Task task = gson.fromJson(request, Task.class);

                        task.setId(id);

                        taskManager.updateTask(task);

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
                if (pathParts[1].equals("tasks") && pathParts.length == 2) {
                    try {
                        taskManager.clearTasks();
                        response = "Задачи успешно удалены!";
                        sendCreated(exchange, response);
                    } catch (ManagerSaveException e) {

                        response = "Ошибка сохранения. Повторите попытку позже";
                        sendInternalServerError(exchange, response);

                    }
                }

                if (pathParts[1].equals("tasks") && pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);

                        taskManager.removeTaskById(id);

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