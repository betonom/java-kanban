package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.managers.HistoryManager;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> tasksHistory;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        tasksHistory = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        removeNode(tasksHistory.get(task.getId()));
        tasksHistory.remove(task.getId());

        Node node = linkLast(task);
        tasksHistory.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        removeNode(tasksHistory.get(id));
        tasksHistory.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private Node linkLast(Task task) {
        Node newTail = new Node(tail, task, null);

        if (tail == null) {
            head = newTail;
        } else {
            tail.next = newTail;
        }
        tail = newTail;

        return newTail;
    }

    private List<Task> getTasks() {
        List<Task> tasksHistoryArrayList = new ArrayList<>();
        //Обход с конца, первым элементом будет последний добавленый
        Node node = tail;
        while (node != null) {
            tasksHistoryArrayList.add(node.data);
            node = node.prev;
        }

        return tasksHistoryArrayList;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
            if (node == tail) {
                tail = node.prev;
            }
        }
        if (node.next != null) {
            node.next.prev = node.prev;
            if (node == head) {
                head = node.next;
            }
        }
        if (node.prev == null && node.next == null) {
            head = null;
            tail = null;
        }
    }

    private static class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}


