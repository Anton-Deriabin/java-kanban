package model;

import manager.TaskType;
import status.Status;

import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> epicSubtusks;

    public Epic(String name, String description) {
        super(name, description);
        this.epicSubtusks = new HashMap<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.epicSubtusks = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getEpicSubtusks() {
        return epicSubtusks;
    }


    public void setEpicSubtusks(HashMap<Integer, Subtask> epicSubtusks) {
        this.epicSubtusks = epicSubtusks;
    }

    public void put(Subtask subtask) {
        epicSubtusks.put(subtask.getId(), subtask);
    }

    public void clear() {
        epicSubtusks.clear();
    }

    public Status isSubtasksDone() {
        int doneCounter = 0;
        Status statusOfAllSubtusks = Status.NEW;
        for (Subtask subtask : epicSubtusks.values()) {
            if (subtask.getStatus() == Status.INPROGRESS) {
                statusOfAllSubtusks = Status.INPROGRESS;
            } else if (subtask.getStatus() == Status.DONE) {
                doneCounter++;
            }
        }
        if (doneCounter == epicSubtusks.size()) {
            statusOfAllSubtusks = Status.DONE;
        }
        return statusOfAllSubtusks;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,", getId(), TaskType.EPIC, getName(), getStatus(), getDescription());
    }

    public static Epic fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType taskType = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        if (taskType == TaskType.EPIC) {
            return new Epic(name, description, id);
        }
        throw new IllegalArgumentException("Неподдерживаемый тип задачи: " + taskType);
    }
}
