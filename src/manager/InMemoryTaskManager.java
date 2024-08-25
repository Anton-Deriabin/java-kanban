package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private static int nextId = 1;
    private final HashMap<Integer, Task> tasksMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasksMap = new HashMap<>();

    @Override
    public Task addTask(Task task) {
        task.setId(nextId);
        nextId++;
        tasksMap.put(task.getId(), task);
        return task;
    }

    @Override
    public HashMap<Integer, Task> printTasks() {
        return tasksMap;
    }

    @Override
    public HashMap<Integer, Task> clearTasks() {
        for (Integer taskKey : tasksMap.keySet()) {
            inMemoryHistoryManager.remove(taskKey);
        }
        tasksMap.clear();
        return tasksMap;
    }

    @Override
    public Task getTask(int id) {
        inMemoryHistoryManager.add(tasksMap.get(id));
        return tasksMap.get(id);
    }

    @Override
    public Task updateTask(Task taskToReplace) {
        if (tasksMap.containsKey(taskToReplace.getId())) {
            tasksMap.replace(taskToReplace.getId(), taskToReplace);
        }
        return taskToReplace;
    }

    @Override
    public HashMap<Integer, Task> deleteTask(int id) {
        inMemoryHistoryManager.remove(id);
        tasksMap.remove(id);
        return tasksMap;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epicsMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public HashMap<Integer, Epic> printEpics() {
        return epicsMap;
    }

    @Override
    public HashMap<Integer, Epic> clearEpics() {
        for (Integer epicKey : epicsMap.keySet()) {
            inMemoryHistoryManager.remove(epicKey);
        }
        for (Integer subtaskKey : subtasksMap.keySet()) {
            inMemoryHistoryManager.remove(subtaskKey);
        }
        for (Integer i : epicsMap.keySet()) {
            epicsMap.get(i).getEpicSubtusks().clear();
        }
        epicsMap.clear();
        subtasksMap.clear();
        return epicsMap;
    }

    @Override
    public Epic getEpic(int id) {
        inMemoryHistoryManager.add(epicsMap.get(id));
        return epicsMap.get(id);
    }

    @Override
    public Epic updateEpic(Epic epicToReplace) {
        epicToReplace.setEpicSubtusks(epicsMap.get(epicToReplace.getId()).getEpicSubtusks());
        if (epicsMap.containsKey(epicToReplace.getId())) {
            epicsMap.replace(epicToReplace.getId(), epicToReplace);
        }
        return epicToReplace;
    }

    @Override
    public HashMap<Integer, Epic> deleteEpic(int id) {
        inMemoryHistoryManager.remove(id);
        for (Integer subtaskKey : epicsMap.get(id).getEpicSubtusks().keySet()) {
            inMemoryHistoryManager.remove(subtaskKey);
        }
        epicsMap.get(id).getEpicSubtusks().clear();
        Iterator<Map.Entry<Integer, Subtask>> iterator = subtasksMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Subtask> entry = iterator.next();
            if (entry.getValue().getSubtasksEpicId() == id) {
                iterator.remove();
            }
        }
        epicsMap.remove(id);
        return epicsMap;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(nextId);
        nextId++;
        subtasksMap.put(subtask.getId(), subtask);
        epicsMap.get(subtask.getSubtasksEpicId()).put(subtask);
        return subtask;
    }

    @Override
    public HashMap<Integer, Subtask> printSubtasks() {
        return subtasksMap;
    }

    @Override
    public HashMap<Integer, Subtask> clearSubtasks() {
        for (Integer subtaskKey : subtasksMap.keySet()) {
            inMemoryHistoryManager.remove(subtaskKey);
        }
        subtasksMap.clear();
        for (Epic epic : epicsMap.values()) {
            epic.clear();
        }
        return subtasksMap;
    }

    @Override
    public Subtask getSubtask(int id) {
        inMemoryHistoryManager.add(subtasksMap.get(id));
        return subtasksMap.get(id);
    }

    @Override
    public Subtask updateSubtask(Subtask subtaskToReplace) {
        if (subtasksMap.containsKey(subtaskToReplace.getId())) {
            subtasksMap.replace(subtaskToReplace.getId(), subtaskToReplace);
        }
        if (epicsMap.get(subtaskToReplace.getSubtasksEpicId()).getEpicSubtusks().
                containsKey(subtaskToReplace.getId())) {
            epicsMap.get(subtaskToReplace.getSubtasksEpicId()).getEpicSubtusks().
                    replace(subtaskToReplace.getId(), subtaskToReplace);
        }
        epicsMap.get(subtaskToReplace.getSubtasksEpicId()).setStatus(epicsMap.get(subtaskToReplace.getSubtasksEpicId()).
                isSubtasksDone());
        return subtaskToReplace;
    }

    @Override
    public HashMap<Integer, Subtask> deleteSubtask(int id) {
        inMemoryHistoryManager.remove(id);
        subtasksMap.remove(id);
        for (Epic epic : epicsMap.values()) {
            Iterator<Subtask> iterator = epic.getEpicSubtusks().values().iterator();
            while (iterator.hasNext()) {
                Subtask subtask = iterator.next();
                if (subtask.getId() == id) {
                    iterator.remove();
                    break;
                }
            }
        }
        return subtasksMap;
    }

    @Override
    public HashMap<Integer, Subtask> printSubtusksOfEpic(Epic epic) {
        return epic.getEpicSubtusks();
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}

