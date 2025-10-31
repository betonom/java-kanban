package com.github.betonom.java_kanban.http.server;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.http.handlers.typeadapters.DurationAdapter;
import com.github.betonom.java_kanban.http.handlers.typeadapters.LocalDateTimeAdapter;
import com.github.betonom.java_kanban.managers.Managers;
import com.github.betonom.java_kanban.managers.TaskManager;
import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

class TaskHandlerTest {
    TaskManager taskManager;
    HttpTaskServer hts;
    HttpClient client;
    HttpResponse<String> response;
    Task newTask;
    Subtask newSubtask;
    Epic newEpic;
    Gson gson;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        hts = new HttpTaskServer(taskManager);
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();


        newTask = new Task("name", "description");
        newTask.setDuration(Duration.ofMinutes(5));
        newTask.setStartTime(LocalDateTime.of(2000, 1, 1, 2, 1));
        taskManager.createNewTask(newTask);

        newEpic = new Epic("name", "description");
        taskManager.createNewEpic(newEpic);

        newSubtask = new Subtask("name", "description", newEpic.getId());
        newSubtask.setDuration(Duration.ofMinutes(5));
        newSubtask.setStartTime(LocalDateTime.of(3000, 1, 1, 1, 1));
        taskManager.createNewSubtask(newSubtask);

        hts.start(8080);
    }

    @AfterEach
    void afterEach() {
        hts.stop();
    }

    @Test
    void tasksGet() {
        URI uri = URI.create("http://localhost:8080/tasks");
        sendGetRequest(uri);

        Assertions.assertEquals(200, response.statusCode(), "Неверный статус код");

        JsonElement je = JsonParser.parseString(response.body());

        Assertions.assertTrue(je.isJsonArray(), "Ответ сервера не соответствует ожидаемому");

        JsonArray taskJsonArr = je.getAsJsonArray();

        Assertions.assertEquals(1, taskJsonArr.size(), "В ответе нет задач");

        JsonElement taskJson = taskJsonArr.get(0);

        Task task = gson.fromJson(taskJson, Task.class);

        Assertions.assertEquals(newTask, task, "Задачи не совпадают");

    }

    @Test
    void tasksIdGet() {
        URI uri = URI.create("http://localhost:8080/tasks/id");
        sendGetRequest(uri);

        Assertions.assertEquals(400, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/tasks/100");
        sendGetRequest(uri);

        Assertions.assertEquals(404, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/tasks/" + newTask.getId());
        sendGetRequest(uri);

        Assertions.assertEquals(200, response.statusCode(), "Неверный статус код");

        JsonElement je = JsonParser.parseString(response.body());

        Assertions.assertTrue(je.isJsonObject(), "Ответ сервера не соответствует ожидаемому");

        Task task = gson.fromJson(je, Task.class);

        Assertions.assertEquals(newTask, task, "Задачи не совпадают");
    }

    @Test
    void tasksPost() {
        Task task = new Task("name1", "desc1");
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.of(2000, 1, 1, 2, 1));

        String requestString = gson.toJson(task);

        URI uri = URI.create("http://localhost:8080/tasks");
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(406, response.statusCode(), "Неверный статус код");

        ////////

        task.setStartTime(LocalDateTime.of(2001, 1, 1, 2, 1));

        requestString = gson.toJson(task);

        uri = URI.create("http://localhost:8080/tasks");
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertNotNull(taskManager.getTasksList().get(1), "Задача не добавилась");
        Assertions.assertEquals("name1", taskManager.getTasksList().get(1).getName(),
                "Добавилась не та задача");
    }

    @Test
    void tasksIdPost() {
        Task task = new Task("name1", "desc1");
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.of(3000, 1, 1, 1, 1));

        String requestString = gson.toJson(task);

        URI uri = URI.create("http://localhost:8080/tasks/" + newTask.getId());
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(406, response.statusCode(), "Неверный статус код");

        ////////

        task.setStartTime(LocalDateTime.of(2001, 1, 1, 2, 1));

        requestString = gson.toJson(task);

        uri = URI.create("http://localhost:8080/tasks/id");
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(400, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/tasks/100");
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(404, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/tasks/" + newTask.getId());
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertEquals("name1", taskManager.getTasksList().get(0).getName(),
                "Задача не обновилась");
    }

    @Test
    void tasksDelete() {
        URI uri = URI.create("http://localhost:8080/tasks");
        sendDeleteRequest(uri);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertEquals(0, taskManager.getTasksList().size(), "Задачи не удалились");
    }

    @Test
    void tasksIdDelete() {
        URI uri = URI.create("http://localhost:8080/tasks/id");
        sendDeleteRequest(uri);

        Assertions.assertEquals(400, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/tasks/10");
        sendDeleteRequest(uri);

        Assertions.assertEquals(404, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/tasks/" + newTask.getId());
        sendDeleteRequest(uri);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertEquals(0, taskManager.getTasksList().size(), "Задачи не удалились");
    }

    ////////////////

    void sendGetRequest(URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        Assertions.assertDoesNotThrow(() -> {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }, "Не удалось сделать запрос на сервер");
    }

    void sendPostRequest(URI uri, String requestString) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestString))
                .uri(uri)
                .build();

        Assertions.assertDoesNotThrow(() -> {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }, "Не удалось сделать запрос на сервер");
    }

    void sendDeleteRequest(URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        Assertions.assertDoesNotThrow(() -> {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }, "Не удалось сделать запрос на сервер");
    }

}

