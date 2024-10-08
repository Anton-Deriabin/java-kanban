package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("Список сохраненных задач:\n");
            for (Task task : tasksMap.values()) {
                writer.write(task.toString() + "\n");
            }
            for (Epic epic : epicsMap.values()) {
                writer.write(epic.toString() + "\n");
            }
            for (Subtask subtask : subtasksMap.values()) {
                writer.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("Ошибка при сохранении данных в файл: %s", file.getName()));
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) {
                String[] fields = line.split(",");
                TaskType taskType = TaskType.valueOf(fields[1]);
                switch (taskType) {
                    case TASK:
                        Task task = Task.fromString(line);
                        manager.addTask(task);
                        break;
                    case EPIC:
                        Epic epic = Epic.fromString(line);
                        manager.addEpic(epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = Subtask.fromString(line);
                        manager.addSubtask(subtask);
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Неизвестный тип задачи: %s", taskType));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("Ошибка при сохранении данных в файл: %s", file.getName()));
        }
        return manager;
    }

    @Override
    public Task addTask(Task task) {
        Task newTask = super.addTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic newEpic = super.addEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask newSubtask = super.addSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public HashMap<Integer, Task> deleteTask(int id) {
        HashMap<Integer, Task> tasks = super.deleteTask(id);
        save();
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> deleteEpic(int id) {
        HashMap<Integer, Epic> epics = super.deleteEpic(id);
        save();
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> deleteSubtask(int id) {
        HashMap<Integer, Subtask> subtasks = super.deleteSubtask(id);
        save();
        return subtasks;
    }

    @Override
    public HashMap<Integer, Task> clearTasks() {
        super.clearTasks();
        save();
        return tasksMap;
    }

    @Override
    public HashMap<Integer, Subtask> clearSubtasks() {
        super.clearSubtasks();
        save();
        return subtasksMap;
    }

    @Override
    public HashMap<Integer, Epic> clearEpics() {
        super.clearEpics();
        save();
        return epicsMap;
    }
}

