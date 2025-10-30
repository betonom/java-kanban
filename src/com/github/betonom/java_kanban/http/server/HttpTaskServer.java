package com.github.betonom.java_kanban.http.server;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.http.handlers.*;
import com.github.betonom.java_kanban.managers.Managers;
import com.github.betonom.java_kanban.managers.TaskManager;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer serv;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) throws IOException {

//        Task newTask2 = new Task("name2", "description2");
//        newTask2.setDuration(Duration.ofMinutes(5));
//        newTask2.setStartTime(LocalDateTime.of(3000, 1, 1, 2, 1));
//        taskManager.createNewTask(newTask2);
//
//        Epic newEpic = new Epic("name", "description");
//        taskManager.createNewEpic(newEpic);
//
//        Subtask newSubtask = new Subtask("name", "description", newEpic.getId());
//        newSubtask.setDuration(Duration.ofMinutes(5));
//        newSubtask.setStartTime(LocalDateTime.of(3000, 1, 1, 1, 1));
//        taskManager.createNewSubtask(newSubtask);
//
//        HttpServer serv = HttpServer.create(new InetSocketAddress(PORT), 0);
//
//        serv.createContext("/tasks", new TaskHandler(taskManager));
//        serv.createContext("/subtasks", new SubtaskHandler(taskManager));
//        serv.createContext("/epics", new EpicHandler(taskManager));
//        serv.createContext("/history", new HistoryHandler(taskManager));
//        serv.createContext("/prioritized", new PrioritizedHandler(taskManager));
//
//        serv.start();
//        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        HttpTaskServer hts = new HttpTaskServer(Managers.getDefault());
        hts.start(PORT);
    }

    public void start(int PORT) {

        try {
            serv = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            System.out.println("Не получилось создать сервер");
        }

        serv.createContext("/tasks", new TaskHandler(taskManager));
        serv.createContext("/subtasks", new SubtaskHandler(taskManager));
        serv.createContext("/epics", new EpicHandler(taskManager));
        serv.createContext("/history", new HistoryHandler(taskManager));
        serv.createContext("/prioritized", new PrioritizedHandler(taskManager));

        serv.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        serv.stop(1);
    }
}
