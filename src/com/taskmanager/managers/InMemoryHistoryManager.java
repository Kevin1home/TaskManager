package com.taskmanager.managers;

import com.taskmanager.tasks.*;

import java.util.*;
import java.util.logging.Logger;

/**
 * InMemoryHistoryManager provides a custom linked-list-based implementation
 * of the {@link HistoryManager} interface.
 * <p>
 * It efficiently supports:
 * - Adding tasks to the end of history (with duplicates removed).
 * - Removing tasks by ID in O(1) time.
 * - Retrieving task history in insertion order.
 * </p>
 */
public class InMemoryHistoryManager implements HistoryManager {

    private static final Logger LOGGER = Logger.getLogger(InMemoryHistoryManager.class.getName());
    private final CustomLinkedList history = new CustomLinkedList();

    @Override
    public void add(Task task) {
        if (task == null) {
            LOGGER.warning("Attempted to add null task to history.");
            return;
        }
        history.linkLast(task);
        LOGGER.info("Task added to history: " + task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = history.getTasks();
        LOGGER.info("Retrieved history with size=" + tasks.size());
        return tasks;
    }

    @Override
    public void remove(int id) {
        if (history.nodes.get(id) == null) {
            LOGGER.info("No node with ID=" + id + "found.");

            return;
        }
        history.removeNode(history.nodes.get(id));
    }

    //-------------------
    // Custom Linked List
    //-------------------
    private static class CustomLinkedList extends LinkedList<Task> {

        private final Map<Integer, Node<Task>> nodes = new HashMap<>();
        private int size = 0;
        private Node<Task> first;
        private Node<Task> last;

        /**
         * Adds a task to the end of the list.
         * If the task already exists, it is moved to the end.
         */
        public void linkLast(Task task) {
            if (nodes.containsKey(task.getId())) {
                removeNode(nodes.get(task.getId()));
            }
            if (size == 0) {
                first = null;
                last = null;
            }

            Node<Task> node = new Node<>(last, task, null);

            if (first == null && last == null) {
                first = node;
            }
            if (last != null) {
                last.next = node;
            }

            last = node;
            nodes.put(task.getId(), node);
            size++;
        }

        /**
         * Returns all tasks in order as a list.
         */
        public List<Task> getTasks() {
            List<Task> viewedTasks = new ArrayList<>();

            if (first == null) {
                return viewedTasks;
            }

            for (Node<Task> node = first; node != null; node = node.next) {
                viewedTasks.add(node.task);
            }
            return viewedTasks;
        }

        /**
         * Removes a node from the linked list.
         */
        private void removeNode(Node<Task> node) {

            if (node == null) {
                return;
            }

            if (node == first && node.next != null) {
                Node<Task> nextNode = node.next;
                nextNode.prev = null;
                first = nextNode;

            } else if (node == first && node.next == null) {
                first = null;
                last = null;

            } else if (node == last && node.prev != null) {
                Node<Task> prevNode = node.prev;
                prevNode.next = null;
                last = prevNode;

            } else {
                Node<Task> nextNode = node.next;
                nextNode.prev = node.prev;
                Node<Task> prevNode = node.prev;
                prevNode.next = node.next;
            }
            size--;
        }

        /**
         * Node class for doubly linked list.
         */
        private static class Node<E> {
            E task;
            Node<E> next;
            Node<E> prev;

            Node(Node<E> prev, E task, Node<E> next) {
                this.task = task;
                this.next = next;
                this.prev = prev;
            }
        }
    }
}