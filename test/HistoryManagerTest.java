import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;

import java.util.List;

public class HistoryManagerTest {
    private final TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    public void historyManagerShouldPutCurrentTasks() {
        /*Task task1 = new Task("Переезд", "Собрать вещи");
        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        Subtask subtask1 = new Subtask("Пролетное строение", "Начертить пролетное строение",
                2);
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.getSubtask(3);
        List<Task> listTest= inMemoryTaskManager.getHistory();
        Assertions.assertEquals(task1, listTest.get(0), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(epic1, listTest.get(1), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(subtask1, listTest.get(2), "Задача в листе не равна вызванной ранее");
        task1.setStatus(Status.INPROGRESS);
        inMemoryTaskManager.getTask(1);
        List<Task> listTest2= inMemoryTaskManager.getHistory();
        Assertions.assertEquals(epic1, listTest2.get(0), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(subtask1, listTest2.get(1), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(task1, listTest2.get(2), "Задача в листе не равна вызванной ранее");
        Task task2 = new Task("Стрижка", "Сходить в барбершоп");
        inMemoryTaskManager.addTask(task2);
        inMemoryTaskManager.getTask(4);
        List<Task> listTest3= inMemoryTaskManager.getHistory();
        Assertions.assertEquals(epic1, listTest3.get(0), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(subtask1, listTest3.get(1), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(task1, listTest3.get(2), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(task2, listTest3.get(3), "Задача в листе не равна вызванной ранее");
        inMemoryTaskManager.deleteTask(1);
        List<Task> listTest4= inMemoryTaskManager.getHistory();
        Assertions.assertEquals(epic1, listTest4.get(0), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(subtask1, listTest4.get(1), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(task2, listTest4.get(2), "Задача в листе не равна вызванной ранее");
        inMemoryTaskManager.deleteSubtask(3);
        List<Task> listTest5= inMemoryTaskManager.getHistory();
        Assertions.assertEquals(epic1, listTest5.get(0), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(task2, listTest5.get(1), "Задача в листе не равна вызванной ранее");
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.getSubtask(5);
        List<Task> listTest6= inMemoryTaskManager.getHistory();
        Assertions.assertEquals(epic1, listTest6.get(0), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(task2, listTest6.get(1), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(subtask1, listTest6.get(2), "Задача в листе не равна вызванной ранее");
        inMemoryTaskManager.deleteEpic(2);
        List<Task> listTest7= inMemoryTaskManager.getHistory();
        Assertions.assertEquals(task2, listTest7.get(0), "Задача в листе не равна вызванной ранее");*/
    }
}
