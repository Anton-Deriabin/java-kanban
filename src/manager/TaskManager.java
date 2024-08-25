package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    HashMap<Integer, Task> printTasks();

    HashMap<Integer, Task> clearTasks();

    Task getTask(int id);

    Task updateTask(Task taskToReplace);

    HashMap<Integer, Task> deleteTask(int id);

    Epic addEpic(Epic epic);

    HashMap<Integer, Epic> printEpics();

    HashMap<Integer, Epic> clearEpics();

    Epic getEpic(int id);

    Epic updateEpic(Epic epicToReplace);

    HashMap<Integer, Epic> deleteEpic(int id);

    Subtask addSubtask(Subtask subtask);

    HashMap<Integer, Subtask> printSubtasks();

    HashMap<Integer, Subtask> clearSubtasks();

    Subtask getSubtask(int id);

    Subtask updateSubtask(Subtask subtaskToReplace);

    HashMap<Integer, Subtask> deleteSubtask(int id);

    HashMap<Integer, Subtask> printSubtusksOfEpic(Epic epic);

    List<Task> getHistory();

     void setNextId(int id);
}
