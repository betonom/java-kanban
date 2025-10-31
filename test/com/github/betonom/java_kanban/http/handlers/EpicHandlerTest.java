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

class EpicHandlerTest {
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
    void epicGet() {
        URI uri = URI.create("http://localhost:8080/epics");
        sendGetRequest(uri);

        Assertions.assertEquals(200, response.statusCode(), "Неверный статус код");

        JsonElement je = JsonParser.parseString(response.body());

        Assertions.assertTrue(je.isJsonArray(), "Ответ сервера не соответствует ожидаемому");

        JsonArray taskJsonArr = je.getAsJsonArray();

        Assertions.assertNotEquals(0, taskJsonArr.size(), "В ответе нет эпиков");

        JsonElement taskJson = taskJsonArr.get(0);

        Epic epic = gson.fromJson(taskJson, Epic.class);

        Assertions.assertEquals(newEpic, epic, "Эпики не совпадают");

    }

    @Test
    void epicsIdGet() {
        URI uri = URI.create("http://localhost:8080/epics/id");
        sendGetRequest(uri);

        Assertions.assertEquals(400, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/epics/100");
        sendGetRequest(uri);

        Assertions.assertEquals(404, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/epics/" + newEpic.getId());
        sendGetRequest(uri);

        Assertions.assertEquals(200, response.statusCode(), "Неверный статус код");

        JsonElement je = JsonParser.parseString(response.body());

        Assertions.assertTrue(je.isJsonObject(), "Ответ сервера не соответствует ожидаемому");

        Epic epic = gson.fromJson(je, Epic.class);

        Assertions.assertEquals(newEpic, epic, "Эпики не совпадают");
    }

    @Test
    void epicsSubtasksGet() {
        URI uri = URI.create("http://localhost:8080/epics/id/subtasks");
        sendGetRequest(uri);

        Assertions.assertEquals(400, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/epics/100/subtasks");
        sendGetRequest(uri);

        Assertions.assertEquals(404, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/epics/" + newEpic.getId() + "/subtasks");
        sendGetRequest(uri);

        Assertions.assertEquals(200, response.statusCode(), "Неверный статус код");

        JsonElement je = JsonParser.parseString(response.body());

        Assertions.assertTrue(je.isJsonArray(), "Ответ сервера не соответствует ожидаемому");

        JsonArray taskJsonArr = je.getAsJsonArray();

        Assertions.assertNotEquals(0, taskJsonArr.size(), "В ответе нет подзадач");

        JsonElement taskJson = taskJsonArr.get(0);

        Subtask subtask = gson.fromJson(taskJson, Subtask.class);

        Assertions.assertEquals(newSubtask, subtask, "Подзадачи не совпадают");
    }

    @Test
    void epicsPost() {
        Epic epic = new Epic("name1", "desc1");

        String requestString = gson.toJson(epic);

        URI uri = URI.create("http://localhost:8080/epics");
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertNotNull(taskManager.getEpicsList().get(1), "Эпик не добавился");
        Assertions.assertEquals("name1", taskManager.getEpicsList().get(1).getName(),
                "Добавился не тот эпик");
    }

    @Test
    void epicsIdPost() {
        Epic epic = new Epic("name1", "desc1");

        String requestString = gson.toJson(epic);

        URI uri = URI.create("http://localhost:8080/epics/id");
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(400, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/epics/100");
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(404, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/epics/" + newEpic.getId());
        sendPostRequest(uri, requestString);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertEquals("name1", taskManager.getEpicsList().get(0).getName(),
                "Эпик не обновился");
    }

    @Test
    void epicsDelete() {
        URI uri = URI.create("http://localhost:8080/epics");
        sendDeleteRequest(uri);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertEquals(0, taskManager.getEpicsList().size(), "Эпики не удалились");
        Assertions.assertEquals(0, taskManager.getSubtasksList().size(), "Подзадачи не удалились");
    }

    @Test
    void epicsIdDelete() {
        URI uri = URI.create("http://localhost:8080/epics/id");
        sendDeleteRequest(uri);

        Assertions.assertEquals(400, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/epics/10");
        sendDeleteRequest(uri);

        Assertions.assertEquals(404, response.statusCode(), "Неверный статус код");

        ////////

        uri = URI.create("http://localhost:8080/epics/" + newEpic.getId());
        sendDeleteRequest(uri);

        Assertions.assertEquals(201, response.statusCode(), "Неверный статус код");

        Assertions.assertEquals(0, taskManager.getEpicsList().size(), "Эпик не удалился");
        Assertions.assertEquals(0, taskManager.getSubtasksList().size(), "Эпик не удалился");
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