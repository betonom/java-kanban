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

class HistoryHandlerTest {
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
    void prioritizedGet() {
        taskManager.getTaskById(newTask.getId());
        taskManager.getSubtaskById(newSubtask.getId());
        taskManager.getEpicById(newEpic.getId());

        URI uri = URI.create("http://localhost:8080/history");
        sendGetRequest(uri);

        Assertions.assertEquals(200, response.statusCode(), "Неверный статус код");

        JsonElement je = JsonParser.parseString(response.body());

        Assertions.assertTrue(je.isJsonArray(), "Ответ сервера не соответствует ожидаемому");

        JsonArray prioritizedJsonArr = je.getAsJsonArray();

        Assertions.assertNotEquals(0, prioritizedJsonArr.size(), "В ответе нет задач");

        JsonElement taskJson1 = prioritizedJsonArr.get(0);
        JsonElement taskJson2 = prioritizedJsonArr.get(1);
        JsonElement taskJson3 = prioritizedJsonArr.get(2);

        Task task = gson.fromJson(taskJson1, Task.class);
        Subtask subtask = gson.fromJson(taskJson2, Subtask.class);
        Epic epic = gson.fromJson(taskJson3, Epic.class);

        Assertions.assertEquals(newTask, task,
                "Задача не добавилась в историю");
        Assertions.assertEquals(newSubtask, subtask,
                "Подзадача не добавилась в историю");
        Assertions.assertEquals(newEpic, epic,
                "Эпик не добавился в историю");

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
}