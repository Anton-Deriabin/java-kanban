import java.util.Scanner;

public class Main {
    static Scanner scanner;

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Переезд", "Собрать вещи");
        Task task2 = new Task("Стрижка", "Сходить в барбершоп");
        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        Subtask subtask1_1 = new Subtask("Пролетное строение", "Начертить пролетное строение");
        Subtask subtask1_2 = new Subtask("Опоры", "Начертить опоры");
        Epic epic2 = new Epic("Командировка", "Подготовиться к командировке");
        Subtask subtask2_1 = new Subtask("Билеты на самолет", "Купить билеты на самолет");
        scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            String command = scanner.nextLine();
            switch (command) {
                case "1":
                    taskManager.addTask(task1);
                    taskManager.addTask(task2);
                    break;
                case "2":
                    System.out.println(taskManager.printTasks().values());
                    break;
                case "3":
                    System.out.println(taskManager.clearTasks());
                    System.out.println("Список задач очищен");
                    break;
                case "4":
                    System.out.println(taskManager.getTask(2));
                    break;
                case "5":
                    Task task3 = new Task("Переезд", "Собрать оставшиеся вещи", Status.IN_PROGRESS);
                    System.out.println(taskManager.updateTask(task1, task3));
                    break;
                case "6":
                    System.out.println(taskManager.deleteTask(1));
                    break;
                case "7":
                    taskManager.addEpic(epic1);
                    taskManager.addEpic(epic2);
                    break;
                case "8":
                    System.out.println(taskManager.printEpics().values());
                    break;
                case "9":
                    System.out.println(taskManager.clearEpics());
                    System.out.println("Список задач очищен");
                    break;
                case "10":
                    System.out.println(taskManager.getEpic(6));
                    break;
                case "11":
                    Epic epic3 = new Epic("Чертежи арочного моста", "Сделать проект моста через реку Волга");
                    System.out.println(taskManager.updateEpic(epic1, epic3));
                    break;
                case "12":
                    System.out.println(taskManager.deleteEpic(3));
                    break;
                case "13":
                    taskManager.addSubtask(subtask1_1, epic1);
                    taskManager.addSubtask(subtask1_2, epic1);
                    taskManager.addSubtask(subtask2_1, epic2);
                    break;
                case "14":
                    System.out.println(taskManager.printSubtasks().values());
                    break;
                case "15":
                    System.out.println(taskManager.clearSubtasks());
                    System.out.println("Список задач очищен");
                    break;
                case "16":
                    System.out.println(taskManager.getSubtask(4));
                    break;
                case "17":
                    Subtask subtask2_2 = new Subtask("Билеты на самолет", "Купить билеты на самолет", Status.DONE);
                    System.out.println(taskManager.updateSubtask(subtask2_1, subtask2_2));
                    break;
                case "18":
                    System.out.println(taskManager.deleteSubtask(5));
                    break;
                case "19":
                    System.out.println(taskManager.printSubtusksOfEpic(epic2));
                    break;
                case "20":
                    System.out.println("Выход из программы");
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
        System.out.println("20 - Выход");
    }
}
