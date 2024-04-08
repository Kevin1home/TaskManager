package managers;

import tasks.*;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList history = new CustomLinkedList();

    private class CustomLinkedList extends LinkedList<Task> {

        private final Map<Integer, Node<Task>> nodes = new HashMap<>();
        private int size = 0;
        private Node<Task> first;
        private Node<Task> last;

        public void linkLast(Task task) { // Добавляет задачу в конец списка
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

        public List<Task> getTasks() { // Собирает задачи в ArrayList
            List<Task> viewedTasks = new ArrayList<>();
            if (first == null) {
                return viewedTasks;
            }

            for (Node<Task> node = first; node != null; node = node.next) {
                viewedTasks.add(node.task);
            }
            return viewedTasks;
        }

        private void removeNode(Node<Task> node) {
            if (node == null) {
                System.out.println("Узла нет");
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

        private class Node<E> {
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

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void remove(int id) {
        if (history.nodes.get(id) == null) {
            System.out.println("Узла для удаления задачи в истории нет");
            return;
        }
        history.removeNode(history.nodes.get(id));
    }

}