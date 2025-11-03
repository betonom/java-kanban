package com.github.betonom.java_kanban.http.server;

import com.github.betonom.java_kanban.http.handlers.*;
import com.github.betonom.java_kanban.managers.Managers;
import com.github.betonom.java_kanban.managers.TaskManager;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer serv;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) {
        HttpTaskServer hts = new HttpTaskServer(Managers.getDefault());
        hts.start(PORT);
    }

    public void start(int port) {

        try {
            serv = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            System.out.println("Не получилось создать сервер");
        }

        serv.createContext("/tasks", new TaskHandler(taskManager));
        serv.createContext("/subtasks", new SubtaskHandler(taskManager));
        serv.createContext("/epics", new EpicHandler(taskManager));
        serv.createContext("/history", new HistoryHandler(taskManager));
        serv.createContext("/prioritized", new PrioritizedHandler(taskManager));

        serv.start();
        System.out.println("HTTP-сервер запущен на " + port + " порту!");
    }

    public void stop() {
        serv.stop(1);
    }
}
