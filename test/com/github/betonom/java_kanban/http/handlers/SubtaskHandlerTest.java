package com.github.betonom.java_kanban.http.handlers;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.http.handlers.typeadapters.DurationAdapter;
import com.github.betonom.java_kanban.http.handlers.typeadapters.LocalDateTimeAdapter;
import com.github.betonom.java_kanban.http.server.HttpTaskServer;
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

class SubtaskHandlerTest {
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
    void subtasksGet() {
        URI uri = URI.create("http://localhost:8080/subtasks");
        sendGetRequest(uri);

        Assertions.assertEquals(200, response.statusCode(), "Неверный статус код");

        JsonElement je = JsonParser.parseString(response.body());

        Assertions.assertTrue(je.isJsonArray(), "Ответ сервера не соответствует ожидаемому");

        JsonArray taskJsonArr = je.getAsJsonArray();

        Assertions.assertEquals(1, taskJsonArr.size(), "В ответе нет подзадач");

        JsonElement taskJson = taskJsonArr.get(0);

        Subtask subtask = gson.fromJson(taskJson, Subtask.class);

        Assertions.assertEquals(newSubtask, subtask, "Подзадачи не совпадают");

    }

    @Test
    void subtasksIdGet() {
        URI uri = URI.create("http://localhost:8080/subtasks/id");
        sendGetRequest(uri);

        Assertions.assertEquals(400, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/subtasks/100");
        sendGetRequest(uri);

        Assertions.assertEquals(404, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/subtasks/" + newSubtask.getId());
        sendGetRequest(uri);

        Assertions.assertEquals(200, response.statusCode(), "Неверный статус код");

        JsonElement je = JsonParser.parseString(response.body());

        Assertions.assertTrue(je.isJsonObject(), "Ответ сервера не соответствует ожидаемому");

        Subtask subtask = gson.fromJson(je, Subtask.class);

        Assertions.assertEquals(newSubtask, subtask, "Подзадачи не совпадают");
    }

    @Test
    void subtasksPost() {
        Subtask subtask = new Subtask("name1", "desc1", newEpic.getId());
        subtask.setDuration(Duration.ofMinutes(5));
        subtask.setStartTime(LocalDateTime.of(2000, 1, 1, 2, 1));

        String requestString = gson.toJson(subtask);

        URI uri = URI.create("http://localhost:8080/subtasks");
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(406, response.statusCode(), "Неверный статус код");

        ////////

        subtask.setStartTime(LocalDateTime.of(2001, 1, 1, 2, 1));

        requestString = gson.toJson(subtask);

        uri = URI.create("http://localhost:8080/subtasks");
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertNotNull(taskManager.getSubtasksList().get(1), "Подзадача не добавилась");
        Assertions.assertEquals("name1", taskManager.getSubtasksList().get(1).getName(),
                "Добавилась не та подзадача");
    }

    @Test
    void subtasksIdPost() {
        Subtask subtask = new Subtask("name1", "desc1", newEpic.getId());
        subtask.setDuration(Duration.ofMinutes(5));
        subtask.setStartTime(LocalDateTime.of(2000, 1, 1, 2, 1));

        String requestString = gson.toJson(subtask);

        URI uri = URI.create("http://localhost:8080/subtasks/" + newSubtask.getId());
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(406, response.statusCode(), "Неверный статус код");

        ////////

        subtask.setStartTime(LocalDateTime.of(2001, 1, 1, 2, 1));

        requestString = gson.toJson(subtask);

        uri = URI.create("http://localhost:8080/subtasks/id");
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(400, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/subtasks/100");
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(404, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/subtasks/" + newSubtask.getId());
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertEquals("name1", taskManager.getSubtasksList().get(0).getName(),
                "Подзадача не обновилась");
    }

    @Test
    void subtasksDelete() {
        URI uri = URI.create("http://localhost:8080/subtasks");
        sendDeleteRequest(uri);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertEquals(0, taskManager.getSubtasksList().size(), "Подзадачи не удалились");
    }

    @Test
    void subtasksIdDelete() {
        URI uri = URI.create("http://localhost:8080/subtasks/id");
        sendDeleteRequest(uri);

        Assertions.assertEquals(400, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/subtasks/10");
        sendDeleteRequest(uri);

        Assertions.assertEquals(404, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/subtasks/" + newSubtask.getId());
        sendDeleteRequest(uri);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertEquals(0, taskManager.getSubtasksList().size(), "Подзадачи не удалились");
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