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
        for (Integer i : epicsMap.keySet()) {
            epicsMap.get(i).getEpicSubtusks().clear();
        }
        epicsMap.clear();
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
        epicsMap.get(id).getEpicSubtusks().clear();
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

