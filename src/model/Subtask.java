package model;

import manager.TaskType;
import status.Status;

public class Subtask extends Task {
    private final int subtasksEpicId;


    public Subtask(String name, String description, int subtasksEpicId) {
        super(name, description);
        this.subtasksEpicId = subtasksEpicId;
    }

    public Subtask(String name, String description, Status status, int id, int subtasksEpicId) {
        super(name, description, status, id);
        this.subtasksEpicId = subtasksEpicId;
    }


    public int getSubtasksEpicId() {
        return subtasksEpicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d", getId(), TaskType.SUBTASK, getName(), getStatus(), getDescription(), subtasksEpicId);
    }

    public static Subtask fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType taskType = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        int epicId = Integer.parseInt(fields[5]);

        if (taskType == TaskType.SUBTASK) {
            return new Subtask(name, description, status, id, epicId);
        }
        throw new IllegalArgumentException(String.format("Неподдерживаемый тип задачи: %s", taskType));
    }
}
