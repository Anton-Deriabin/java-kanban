package main;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import status.Status;

import java.util.Scanner;

public class Main {
    static Scanner scanner;

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        Task task1 = new Task("Переезд", "Собрать вещи");
        Task task2 = new Task("Стрижка", "Сходить в барбершоп");
        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        Epic epic2 = new Epic("Командировка", "Подготовиться к командировке");
        Subtask subtask11 = new Subtask("Пролетное строение", "Начертить пролетное строение",
                3);
        Subtask subtask12 = new Subtask("Опоры", "Начертить опоры", 3);
        Subtask subtask21 = new Subtask("Билеты на самолет", "Купить билеты на самолет",
                4);
        scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            String command = scanner.nextLine();
            switch (command) {
                case "1":
                    inMemoryTaskManager.addTask(task1);
                    inMemoryTaskManager.addTask(task2);
                    break;
                case "2":
                    System.out.println(inMemoryTaskManager.printTasks().values());
                    break;
                case "3":
                    System.out.println(inMemoryTaskManager.clearTasks());
                    break;
                case "4":
                    System.out.println(inMemoryTaskManager.getTask(1));
                    break;
                case "5":
                    Task task3 = new Task("Переезд. продолжение", "Собрать оставшиеся вещи",
                            Status.INPROGRESS, task1.getId());
                    System.out.println(inMemoryTaskManager.updateTask(task3));
                    break;
                case "6":
                    System.out.println(inMemoryTaskManager.deleteTask(1));
                    break;
                case "7":
                    inMemoryTaskManager.addEpic(epic1);
                    inMemoryTaskManager.addEpic(epic2);
                    break;
                case "8":
                    System.out.println(inMemoryTaskManager.printEpics().values());
                    break;
                case "9":
                    System.out.println(inMemoryTaskManager.clearEpics());
                    break;
                case "10":
                    System.out.println(inMemoryTaskManager.getEpic(3));
                    break;
                case "11":
                    Epic epic3 = new Epic("Чертежи арочного моста", "Сделать проект моста через реку " +
                            "Волга", epic1.getId());
                    System.out.println(inMemoryTaskManager.updateEpic(epic3));
                    break;
                case "12":
                    System.out.println(inMemoryTaskManager.deleteEpic(3));
                    break;
                case "13":
                    inMemoryTaskManager.addSubtask(subtask11);
                    inMemoryTaskManager.addSubtask(subtask12);
                    inMemoryTaskManager.addSubtask(subtask21);
                    break;
                case "14":
                    System.out.println(inMemoryTaskManager.printSubtasks().values());
                    break;
                case "15":
                    System.out.println(inMemoryTaskManager.clearSubtasks());
                    break;
                case "16":
                    System.out.println(inMemoryTaskManager.getSubtask(5));
                    break;
                case "17":
                    Subtask subtask22 = new Subtask("Билеты на самолет", "Купить билеты на самолет",
                            Status.DONE, subtask21.getId(), epic2.getId());
                    System.out.println(inMemoryTaskManager.updateSubtask(subtask22));
                    break;
                case "18":
                    System.out.println(inMemoryTaskManager.deleteSubtask(5));
                    break;
                case "19":
                    System.out.println(inMemoryTaskManager.printSubtusksOfEpic(epic1));
                    System.out.println(inMemoryTaskManager.printSubtusksOfEpic(epic2));
                    break;
                case "20":
                    System.out.println(inMemoryTaskManager.getHistory());
                    break;
                case "21":
                    System.out.println("Выход");
                    return;
            }
        }
    }

    private static void printMenu() {
        System.out.println("Выберите команду:");
        System.out.println("1 - Добавить задачу в список");
        System.out.println("2 - Посмотреть список задач");
        System.out.println("3 - Очистить список задач");
        System.out.println("4 - Получить задачу по id");
        System.out.println("5 - Обновить статус задачи");
        System.out.println("6 - Удалить задачу");
        System.out.println("7 - Добавить эпик в список");
        System.out.println("8 - Посмотреть список эпиков");
        System.out.println("9 - Очистить список эпиков");
        System.out.println("10 - Получить эпик по id");
        System.out.println("11 - Обновить статус эпика");
        System.out.println("12 - Удалить эпик");
        System.out.println("13 - Добавить подзадачу в эпик");
        System.out.println("14 - Посмотреть список всех подзадач всех эпиков");
        System.out.println("15 - Очистить список всех подзадач всех эпиков");
        System.out.println("16 - Получить подзадачу по id");
        System.out.println("17 - Обновить статус подзадачи");
        System.out.println("18 - Удалить подзадачу");
        System.out.println("19 - Посмотреть список подзадач эпика");
        System.out.println("20 - Запрос истории просмотров");
        System.out.println("21 - Выход");
    }
}


