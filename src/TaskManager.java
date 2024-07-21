import java.util.HashMap;
import java.util.Iterator;

public class TaskManager {
    private final HashMap<Integer, Task> tasksMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasksMap = new HashMap<>();

    public Task addTask(Task task) {
        tasksMap.put(task.getId(), task);
        return task;
    }

    public HashMap<Integer, Task> printTasks() {
        return tasksMap;
    }

    public HashMap<Integer, Task> clearTasks() {
        tasksMap.clear();
        return tasksMap;
    }

    public Task getTask(int id) {
        return tasksMap.get(id);
    }

    public Task updateTask(Task replacedTask, Task taskToReplace) {
        taskToReplace.setId(replacedTask.getId());
        tasksMap.replace(replacedTask.getId(), replacedTask, taskToReplace);
        return taskToReplace;
    }

    public HashMap<Integer, Task> deleteTask(int id) {
        tasksMap.remove(id);
        return tasksMap;
    }

    public Epic addEpic(Epic epic) {
        epicsMap.put(epic.getId(), epic);
        return epic;
    }

    public HashMap<Integer, Epic> printEpics() {
        return epicsMap;
    }

    public HashMap<Integer, Epic> clearEpics() {
        for (Epic epic : epicsMap.values()) {
            for (Subtask subtask : epic.getEpicSubtusks().values()) {
                subtasksMap.remove(subtask.getId());
            }
        }
        epicsMap.clear();
        return epicsMap;
    }

    public Epic getEpic(int id) {
        return epicsMap.get(id);
    }

    public Epic updateEpic(Epic replacedEpic, Epic epicToReplace) {
        epicToReplace.setId(replacedEpic.getId());
        epicToReplace.setEpicSubtusks(replacedEpic.getEpicSubtusks());
        epicsMap.replace(replacedEpic.getId(), replacedEpic, epicToReplace);
        return epicToReplace;
    }

    public HashMap<Integer, Epic> deleteEpic(int id) {
        Epic epicToRemove = epicsMap.get(id);
        if (epicToRemove != null) {
            for (Subtask subtask : epicToRemove.getEpicSubtusks().values()) {
                subtasksMap.remove(subtask.getId());
            }
        }
        epicsMap.remove(id);
        return epicsMap;
    }

    public Subtask addSubtask(Subtask subtask, Epic epic) {
        subtasksMap.put(subtask.getId(), subtask);
        epic.put(subtask);
        return subtask;
    }

    public HashMap<Integer, Subtask> printSubtasks() {
        return subtasksMap;
    }

    public HashMap<Integer, Subtask> clearSubtasks() {
        subtasksMap.clear();
        for (Epic epic : epicsMap.values()) {
            epic.clear();
        }
        return subtasksMap;
    }

    public Subtask getSubtask(int id) {
        return subtasksMap.get(id);
    }

    public Subtask updateSubtask(Subtask replacedSubtask, Subtask subtaskToReplace) {
        subtaskToReplace.setId(replacedSubtask.getId());
        subtasksMap.replace(replacedSubtask.getId(), replacedSubtask, subtaskToReplace);

        for (Epic epic : epicsMap.values()) {
            if (epic.containsKey(replacedSubtask.getId())) {
                epic.replace(replacedSubtask.getId(), replacedSubtask, subtaskToReplace);
            }
        }

        for (Epic epic : epicsMap.values()) {
            if (epic.containsKey(replacedSubtask.getId())) {
                epic.setStatus(epic.isSubtasksDone());
            }

        }
        return subtaskToReplace;
    }

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

    public HashMap<Integer, Subtask> printSubtusksOfEpic(Epic epic) {
        return epic.getEpicSubtusks();
    }
}

