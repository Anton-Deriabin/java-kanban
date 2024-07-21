import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasksMap = new HashMap<>();
    private HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtasksMap = new HashMap<>();


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

    public Task updateTask(Task task, Status status) {
        tasksMap.get(task.getId()).setStatus(status);
        return task;
    }

    public HashMap<Integer, Task> deleteTask(int id) {
        tasksMap.remove(id);
        return tasksMap;
    }

    //---------------------------------------------------------------

    public Epic addEpic(Epic epic) {
        epicsMap.put(epic.getId(), epic);
        return epic;
    }

    public HashMap<Integer, Epic> printEpics() {
        return epicsMap;
    }

    public HashMap<Integer, Epic> clearEpics() {
        epicsMap.clear();
        return epicsMap;
    }

    public Epic getEpic(int id) {
        return epicsMap.get(id);
    }

    public Epic updateEpic(Epic epic, Status status) {
        epicsMap.get(epic.getId()).setStatus(status);
        return epic;
    }

    public HashMap<Integer, Epic> deleteEpic(int id) {
        epicsMap.remove(id);
        return epicsMap;
    }

    //---------------------------------------------------------------

    public Subtask addSubtask(Subtask subtask) {
        subtasksMap.put(subtask.getId(), subtask);
        return subtask;
    }

    public HashMap<Integer, Subtask> printSubtasks() {
        return subtasksMap;
    }

    public HashMap<Integer, Subtask> clearSubtasks() {
        subtasksMap.clear();
        return subtasksMap;
    }

    public Subtask getSubtask(int id) {
        return subtasksMap.get(id);
    }

    public Subtask updateSubtask(Subtask subtask, Status status) {
        subtasksMap.get(subtask.getId()).setStatus(status);
        return subtask;
    }

    public HashMap<Integer, Subtask> deleteSubtask(int id) {
        subtasksMap.remove(id);
        return subtasksMap;
    }
}

/*
Менеджер
Кроме классов для описания задач, вам нужно реализовать класс для объекта-менеджера.
Он будет запускаться на старте программы и управлять всеми задачами.
В нём должны быть реализованы следующие функции:

Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.

Методы для каждого из типа задач(Задача/Эпик/Подзадача):
 a. Получение списка всех задач.
 b. Удаление всех задач.
 c. Получение по идентификатору.
 d. Создание. Сам объект должен передаваться в качестве параметра.
 e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
 f. Удаление по идентификатору.

Дополнительные методы:
a. Получение списка всех подзадач определённого эпика.

Управление статусами осуществляется по следующему правилу:
 a. Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче.
 По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.
 b. Для эпиков:
если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
во всех остальных случаях статус должен быть IN_PROGRESS.
Подсказки
 */
