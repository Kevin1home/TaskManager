package managers;

import api.KVTaskClient;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import tasks.*;

import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager { // Логика сохранения на сервер

    public static KVTaskClient kvTaskClient = null;
    static final Gson gson = new Gson();

    @Override
    protected void save() {

        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                String jsonTask = gson.toJson(task);
                kvTaskClient.put("ID_" + task.getId(), jsonTask);

                System.out.println("Сохранили: " + jsonTask);
            }
        }

        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                String jsonEpic = gson.toJson(epic);
                kvTaskClient.put("ID_" + epic.getId(), jsonEpic);

                System.out.println("Сохранили: " + jsonEpic);
            }
        }

        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks.values()) {
                String jsonSubtask = gson.toJson(subtask);
                kvTaskClient.put("ID_" + subtask.getId(), jsonSubtask);

                System.out.println("Сохранили: " + jsonSubtask);
            }
        }

        if (!historyManager.getHistory().isEmpty()) {
            String jsonHistory = gson.toJson(historyManager.getHistory());
            kvTaskClient.put("History", jsonHistory);

            System.out.println("Сохранили: " + jsonHistory);
        }

        String jsonId = gson.toJson(nextId);
        kvTaskClient.put("NextId", jsonId);
        System.out.println("Сохранили последнее ID: " + jsonId);
    }

    public static HttpTaskManager load(int kVServerPort) {
        if (kvTaskClient == null) {
            kvTaskClient = new KVTaskClient(kVServerPort);
        }

        String jsonNextId = kvTaskClient.load("NextId");

        if (!jsonNextId.isBlank() && !jsonNextId.equals("1")) {
            JsonElement jsonElementNextId = JsonParser.parseString(jsonNextId);
            nextId = jsonElementNextId.getAsInt();

            for (int i = 1; i < nextId; i++) {
                String jsonElementString = kvTaskClient.load("ID_" + i);

                if (!jsonElementString.isBlank()) {
                    JsonElement jsonElement = JsonParser.parseString(jsonElementString);
                    System.out.println("Получили от ключа ID_" + i + ": ");
                    System.out.println(jsonElement);

                    if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                        System.out.println("Получен не объект.");
                        System.out.println("Ответ от сервера не соответствует ожидаемому.");
                        System.out.println("Загрузка данных прервана.");
                        return new HttpTaskManager();
                    }

                    JsonObject jsonObjectSomeTask = jsonElement.getAsJsonObject();
                    String taskType = jsonObjectSomeTask.get("type").getAsString();

                    switch (taskType) {
                        case "TASK":
                            Task task = gson.fromJson(jsonObjectSomeTask, Task.class);
                            tasks.put(task.getId(), task);
                            break;
                        case "EPIC":
                            Epic epic = gson.fromJson(jsonObjectSomeTask, Epic.class);
                            epics.put(epic.getId(), epic);
                            break;
                        case "SUBTASK":
                            Subtask subtask = gson.fromJson(jsonObjectSomeTask, Subtask.class);
                            subtasks.put(subtask.getId(), subtask);
                            break;
                        default:
                            System.out.println("Ошибка! Передан неверный тип задачи.");
                            System.out.println("Загрузка данных прервана.");
                            return new HttpTaskManager();
                    }
                }
            }

            String jsonHistory = kvTaskClient.load("History");

            if (!jsonHistory.isBlank()) {
                List<Task> history = gson.fromJson(jsonHistory, new TypeToken<ArrayList<Task>>() {
                }.getType());

                for (Task task : history) {
                    historyManager.add(task);
                }
            }
        }
        return new HttpTaskManager();
    }

}