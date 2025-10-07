package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.managers.HistoryManager;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    //private final ArrayList<Task> tasksHistory;

    private Map<Integer, Node<Task>> tasksHistory;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        tasksHistory = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (tasksHistory.containsKey(task.getId())) {
            removeNode(tasksHistory.get(task.getId()));
            tasksHistory.remove(task.getId());
        }
        Node<Task> node = linkLast(task);
        tasksHistory.put(task.getId(), node);
    }

    public Node<Task> linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newTail = new Node<>(oldTail, task, null);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }

        return newTail;
    }

    private List<Task> getTasks() {
        List<Task> tasksHistoryArrayList = new ArrayList<>();
        for (Node<Task> item : tasksHistory.values()) {
            tasksHistoryArrayList.add(item.data);
        }

        return tasksHistoryArrayList;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
    }

    public void remove(int id) {
        removeNode(tasksHistory.get(id));
        tasksHistory.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}


