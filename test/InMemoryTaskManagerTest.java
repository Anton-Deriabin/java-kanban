import exceptions.NotFoundException;
import manager.InMemoryTaskManager;
import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    protected Task task3;
    protected Task task4;
    protected Epic epic3;
    protected Subtask subtask3;
    protected Subtask subtask4;
    protected Subtask subtask5;

    @BeforeEach
    @Override
    public void setUp() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        task2 = new Task("Стрижка",
                "Сходить в барбершоп",
                Duration.ofHours(3),
                LocalDateTime.of(2024, 9, 24, 17, 0));
        epic1 = new Epic("Переезд", "Собрать вещи");
        epic2 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        subtask1 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                1,
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        subtask2 = new Subtask("Опоры",
                "Начертить опоры",
                1,
                Duration.ofDays(8),
                LocalDateTime.of(2024, 10, 28, 8, 0));
    }

    @AfterEach
    @Override
    public void setNextId() {
        super.setNextId();
    }

    @Test
    @Override
    public void taskManagerShouldAddTask() {
        taskManager.addTask(task1);
        Optional task2Optional = taskManager.getTask(task1.getId());
        Task task2 = (Task) task2Optional.get();
        Assertions.assertEquals(task1, task2, "Задачи не равны");
    }

    @Test
    @Override
    public void taskManagerShouldDeleteTask() {
        taskManager.addTask(task1);
        taskManager.deleteTask(task1.getId());
        assertThrows(NotFoundException.class, () -> {
            taskManager.printTasks();
        }, "Задача не удалена");
    }

    @Test
    @Override
    public void taskManagerShouldClearTasks() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.clearTasks();
        assertThrows(NotFoundException.class, () -> {
            taskManager.printTasks();
        }, "Список задач не очищен");

    }

    @Test
    @Override
    public void taskManagerShouldUpdateTask() {
        taskManager.addTask(task1);
        task2 = new Task("Переезд",
                "Собрать вещи",
                task1.getId(),
                Status.NEW,
                task1.getDuration(),
                task1.getStartTime());
        Assertions.assertEquals(task1, task2, "Задачи не равны");
        task3 = new Task("Переезд",
                "Собрать вещи",
                task1.getId(),
                Status.INPROGRESS,
                task1.getDuration(),
                task1.getStartTime());
        taskManager.updateTask(task3);
        task2.setStatus(Status.INPROGRESS);
        Assertions.assertEquals(task2, task3, "Задачи не равны");
        task4 = new Task("Переезд",
                "Собрать вещи",
                task1.getId(),
                Status.DONE,
                task1.getDuration(),
                task1.getStartTime());
        taskManager.updateTask(task4);
        task2.setStatus(Status.DONE);
        Assertions.assertEquals(task2, task4, "Задачи не равны");
    }

    @Test
    @Override
    public void taskManagerShouldAddEpic() {
        taskManager.addEpic(epic1);
        Optional epic2Optional = taskManager.getEpic(epic1.getId());
        Epic epic2 = (Epic) epic2Optional.get();
        Assertions.assertEquals(epic1, epic2, "Эпики не равны");
    }

    @Test
    @Override
    public void taskManagerShouldDeleteEpic() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.deleteEpic(epic2.getId());
        assertThrows(NotFoundException.class, () -> {
            taskManager.printEpics();
        }, "Эпик не удален");
        assertThrows(NotFoundException.class, () -> {
            taskManager.printSubtasks();
        }, "Подзадача не удалена вместе с эпиком");

    }

    @Test
    @Override
    public void taskManagerShouldClearEpics() {
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.clearEpics();
        assertThrows(NotFoundException.class, () -> {
            taskManager.printEpics();
        }, "Список эпиков не очищен");
        assertThrows(NotFoundException.class, () -> {
            taskManager.printSubtasks();
        }, "Список подзадач не очищен");
    }

    @Test
    @Override
    public void taskManagerShouldUpdateEpic() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        epic3 = new Epic("Чертежи моста",
                "Сделать проект моста через реку Кама",
                epic2.getId(),
                epic2.getStatus(),
                epic2.getDuration(),
                epic2.getStartTime());
        taskManager.updateEpic(epic3);
        Assertions.assertEquals(taskManager.printEpics().get(epic2.getId()), epic3, "Эпики не равны");
    }

    @Test
    @Override
    public void taskManagerShouldAddSubtusk() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        Optional subtask1Optional = taskManager.getSubtask(subtask1.getId());
        Subtask subtask2 = (Subtask) subtask1Optional.get();
        Assertions.assertEquals(subtask1, subtask2, "Подзадачи не равны");
        Assertions.assertEquals(epic2.getEpicSubtusks().get(subtask1.getId()), subtask2, "Подзадачи не равны");
    }

    @Test
    @Override
    public void taskManagerShouldDeleteSubtusk() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.deleteSubtask(subtask1.getId());
        Assertions.assertTrue(taskManager.printEpics().get(epic2.getId()).getEpicSubtusks().isEmpty(),
                "Подзадача не удалена из своего эпика");
        assertThrows(NotFoundException.class, () -> {
            taskManager.printSubtasks();
        }, "Подзадача не удалена из списка подзадач");
    }

    @Test
    @Override
    public void taskManagerShouldClearSubtusks() {
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.clearSubtasks();
        Assertions.assertTrue(taskManager.printEpics().get(epic2.getId()).getEpicSubtusks().isEmpty(),
                "Подзадачи не очищены из эпика");
        assertThrows(NotFoundException.class, () -> {
            taskManager.printSubtasks();
        }, "Подзадачи не очищены из списка подзадач");
    }

    @Test
    @Override
    public void taskManagerShouldUpdateSubtusk() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        epic3 = new Epic("Чертежи моста",
                "Сделать проект моста через реку Волга",
                epic2.getId(),
                epic2.getStatus(),
                epic2.getDuration(),
                epic2.getStartTime());
        Assertions.assertEquals(epic2, epic3, "Эпики не равны");
        subtask3 = new Subtask(subtask1.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                Status.INPROGRESS,
                subtask1.getSubtasksEpicId(),
                subtask1.getDuration(),
                subtask1.getStartTime());
        taskManager.updateSubtask(subtask3);
        epic3.setStatus(Status.INPROGRESS);
        Assertions.assertEquals(epic2, epic3, "Эпики не равны");
        subtask4 = new Subtask(subtask1.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                Status.DONE,
                subtask1.getSubtasksEpicId(),
                subtask1.getDuration(),
                subtask1.getStartTime());
        taskManager.updateSubtask(subtask4);
        Assertions.assertEquals(epic2, epic3, "Эпики не равны");
        subtask5 = new Subtask(subtask2.getId(),
                "Опоры",
                "Начертить опоры",
                Status.DONE,
                subtask2.getSubtasksEpicId(),
                subtask2.getDuration(),
                subtask2.getStartTime());
        taskManager.updateSubtask(subtask5);
        epic3.setStatus(Status.DONE);
        Assertions.assertEquals(epic2, epic3, "Эпики не равны");
    }

}
