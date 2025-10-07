package com.github.betonom.java_kanban.managers.inmemory;

class Node<V> {
    public V data;
    public Node<V> next;
    public Node<V> prev;

    public Node(Node<V> prev, V data, Node<V> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
