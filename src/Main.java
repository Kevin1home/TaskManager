import api.HttpTaskServer;
import api.KVServer;
import com.google.gson.Gson;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("ТЕСТИРОВАНИЕ");

        // Запустить сервера
        System.out.println("Запускаем сервер");
        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer1 = new HttpTaskServer();
        httpTaskServer1.start();

        // Создать клиента
        System.out.println("Создаём конечного клиента");
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        // Создать запрос на создание обычной задачи
        System.out.println("\nСоздаём запрос на создание обычной задачи");
        URI url1 = URI.create("http://localhost:8080/tasks/task/");
        Task task1 = new Task("Task", "DescrT", TaskStatus.NEW, "30.06.2024, 09:30", 60L);
        System.out.println("Создали задачу " + task1);
        String jsonTask1 = gson.toJson(task1);
        System.out.println("Перевели в json " + task1);
        HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        System.out.println("Ответ:");
        System.out.println(response1.statusCode() + " " + response1.body());

        // Создать запрос на создание эпика
        System.out.println("\nСоздаём запрос на создание эпика");
        URI url2 = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic2 = new Epic("Epic1", "DescrE1");
        System.out.println("Создали эпик " + epic2);
        String jsonEpic2 = gson.toJson(epic2);
        System.out.println("Перевели в json " + epic2);
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonEpic2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        System.out.println("Ответ:");
        System.out.println(response2.statusCode() + " " + response2.body());

        // Создать запрос на создание подзадачи
        System.out.println("\nСоздаём запрос на создание подзадачи");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/");
        Subtask subtask3 = new Subtask("Subtask1", "DescrS1", 2);
        System.out.println("Создали подзадачу " + subtask3);
        String jsonSubtask2 = gson.toJson(subtask3);
        System.out.println("Перевели в json " + subtask3);
        HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(body3).build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        System.out.println("Ответ:");
        System.out.println(response3.statusCode() + " " + response3.body());

        // Создать запрос на получение всех задач всех типов
        System.out.println("\nСоздаём запрос на получение всех задач всех типов");
        URI url4 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        System.out.println(response4.statusCode() + " " + response4.body());

        // Создать запрос на получение подзадач эпика
        System.out.println("\nСоздаём запрос на получение подзадач эпика");
        URI url5 = URI.create("http://localhost:8080/tasks/subtask/epic?id=2");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        System.out.println(response5.statusCode() + " " + response5.body());

        // Перезапускаем сервер HttpTaskServer
        httpTaskServer1.stop();
        HttpTaskServer httpTaskServer2 = new HttpTaskServer();
        httpTaskServer2.start();

        // Создать запрос на получение всех задач всех типов
        System.out.println("\nСоздаём запрос на получение всех задач всех типов");
        URI url6 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request6 = HttpRequest.newBuilder().uri(url6).GET().build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
        System.out.println(response6.statusCode() + " " + response6.body());

        // Создать запрос на получение подзадач эпика
        System.out.println("\nСоздаём запрос на получение подзадач эпика");
        URI url7 = URI.create("http://localhost:8080/tasks/subtask/epic?id=2");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
        System.out.println(response7.statusCode() + " " + response7.body());

        httpTaskServer2.stop();
        kvServer.stop();
    }

}