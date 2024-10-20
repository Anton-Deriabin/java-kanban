package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import status.Status;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private static int nextId = 1;
    protected final HashMap<Integer, Task> tasksMap = new HashMap<>();
    protected final HashMap<Integer, Epic> epicsMap = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasksMap = new HashMap<>();
    private TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));

    @Override
    public Task addTask(Task task) {
        if (isOverlapping(task)) {
            throw new IllegalArgumentException("Задачи пересекаются по времени выполнения.");
        }
        task.setId(nextId);
        nextId++;
        tasksMap.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public HashMap<Integer, Task> printTasks() {
        if (tasksMap.isEmpty()) {
            throw new NotFoundException("Список задач пуст.");
        }
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
    public Optional<Task> getTask(int id) {
        Optional<Task> findTask = tasksMap.values().stream()
                .filter(task -> id == task.getId())
                .findFirst();
        if (findTask.isEmpty()) {
            throw new NotFoundException(String.format("Задача с ID %d не найдена.", id));
        }
        findTask.ifPresent(task -> inMemoryHistoryManager.add(task));
        return findTask;
    }

    @Override
    public Task updateTask(Task taskToReplace) {
        if (tasksMap.containsKey(taskToReplace.getId())) {
            tasksMap.replace(taskToReplace.getId(), taskToReplace);
        } else {
            throw new NotFoundException(String.format("Задача с ID %d не найдена.", taskToReplace.getId()));
        }
        return taskToReplace;
    }

    @Override
    public HashMap<Integer, Task> deleteTask(int id) {
        if (!tasksMap.containsKey(id)) {
            throw new NotFoundException(String.format("Задача с ID %d не найдена.", id));
        }
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
        if (epicsMap.isEmpty()) {
            throw new NotFoundException("Список эпиков пуст.");
        }
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
    public Optional<Epic> getEpic(int id) {
        Optional<Epic> findEpic = epicsMap.values().stream()
                .filter(epic -> id == epic.getId())
                .findFirst();
        if (findEpic.isEmpty()) {
            throw new NotFoundException(String.format("Эпик с ID %d не найден.", id));
        }
        findEpic.ifPresent(epic -> inMemoryHistoryManager.add(epic));
        return findEpic;
    }

    @Override
    public Epic updateEpic(Epic epicToReplace) {
        epicToReplace.setEpicSubtusks(epicsMap.get(epicToReplace.getId()).getEpicSubtusks());
        if (epicsMap.containsKey(epicToReplace.getId())) {
            Status statusEpicToReplace = epicsMap.get(epicToReplace.getId()).getStatus();
            epicsMap.replace(epicToReplace.getId(), epicToReplace);
            epicsMap.get(epicToReplace.getId()).setStatus(statusEpicToReplace);
            epicsMap.get(epicToReplace.getId()).updateEpicTime();
        } else {
            throw new NotFoundException(String.format("Эпик с ID %d не найден.", epicToReplace.getId()));
        }
        return epicToReplace;
    }

    @Override
    public HashMap<Integer, Epic> deleteEpic(int id) {
        if (!epicsMap.containsKey(id)) {
            throw new NotFoundException(String.format("Эпик с ID %d не найден.", id));
        }
        inMemoryHistoryManager.remove(id);
        epicsMap.get(id)
                .getEpicSubtusks()
                .keySet()
                .forEach(inMemoryHistoryManager::remove);
        epicsMap.get(id).getEpicSubtusks().clear();
        subtasksMap.entrySet().removeIf(entry -> entry.getValue().getSubtasksEpicId() == id);
        epicsMap.remove(id);
        return epicsMap;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (isOverlapping(subtask)) {
            throw new IllegalArgumentException("Задачи пересекаются по времени выполнения.");
        }
        subtask.setId(nextId);
        nextId++;
        subtasksMap.put(subtask.getId(), subtask);
        if (epicsMap.get(subtask.getSubtasksEpicId()) == null) {
            throw new IllegalArgumentException(String.format("Эпик с ID %d не найден.", subtask.getSubtasksEpicId()));
        }
        epicsMap.get(subtask.getSubtasksEpicId()).put(subtask);
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public HashMap<Integer, Subtask> printSubtasks() {
        if (subtasksMap.isEmpty()) {
            throw new NotFoundException("Список подзадач пуст.");
        }
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
    public Optional<Subtask> getSubtask(int id) {
        Optional<Subtask> findSubtask = subtasksMap.values().stream()
                .filter(subtask -> id == subtask.getId())
                .findFirst();
        if (findSubtask.isEmpty()) {
            throw new NotFoundException(String.format("Подзадача с ID %d не найдена.", id));
        }
        findSubtask.ifPresent(subtask -> inMemoryHistoryManager.add(subtask));
        inMemoryHistoryManager.add(subtasksMap.get(id));
        return findSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtaskToReplace) {
        if (subtasksMap.containsKey(subtaskToReplace.getId())) {
            subtasksMap.replace(subtaskToReplace.getId(), subtaskToReplace);
        } else {
            throw new NotFoundException(String.format("Подзадача с ID %d не найдена.", subtaskToReplace.getId()));
        }
        if (epicsMap.get(subtaskToReplace.getSubtasksEpicId()).getEpicSubtusks()
                .containsKey(subtaskToReplace.getId())) {
            epicsMap.get(subtaskToReplace.getSubtasksEpicId()).getEpicSubtusks()
                    .replace(subtaskToReplace.getId(), subtaskToReplace);
        } else {
            throw new NotFoundException(String.format("Подзадача с ID %d не найдена.", subtaskToReplace.getId()));
        }
        epicsMap.get(subtaskToReplace.getSubtasksEpicId()).setStatus(epicsMap.get(subtaskToReplace.getSubtasksEpicId())
                .isSubtasksDone());
        return subtaskToReplace;
    }

    @Override
    public HashMap<Integer, Subtask> deleteSubtask(int id) {
        if (!subtasksMap.containsKey(id)) {
            throw new NotFoundException(String.format("Подзадача с ID %d не найдена.", id));
        }
        inMemoryHistoryManager.remove(id);
        subtasksMap.remove(id);
        epicsMap.values().forEach(epic ->
                epic.getEpicSubtusks()
                        .values()
                        .removeIf(subtask -> subtask.getId() == id));
        return subtasksMap;
    }

    @Override
    public HashMap<Integer, Subtask> printSubtusksOfEpic(Epic epic) {
        if (epic.getEpicSubtusks().isEmpty()) {
            throw new NotFoundException("Список подзадач эпика пуст.");
        }
        return epic.getEpicSubtusks();
    }

    @Override
    public List<Task> getHistory() {
        if (inMemoryHistoryManager.getHistory().isEmpty()) {
            throw new NotFoundException("История пуста");
        }
        return inMemoryHistoryManager.getHistory();
    }

    public void setNextId(int nextId) {
        InMemoryTaskManager.nextId = nextId;
    }

    public boolean isOverlapping(Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(existingTask ->
                        existingTask.getStartTime() != null &&
                                existingTask.getEndTime() != null &&
                                newTask.getStartTime().isBefore(existingTask.getStartTime()) &&
                                newTask.getEndTime().isAfter(existingTask.getStartTime()) ||
                                newTask.getStartTime().isBefore(existingTask.getEndTime()) &&
                                        newTask.getEndTime().isAfter(existingTask.getEndTime()) ||
                                newTask.getStartTime().isAfter(existingTask.getStartTime()) &&
                                        newTask.getEndTime().isBefore(existingTask.getEndTime()) ||
                                newTask.getStartTime().isBefore(existingTask.getStartTime()) &&
                                        newTask.getEndTime().isAfter(existingTask.getEndTime()));
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }
}
