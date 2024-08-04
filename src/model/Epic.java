package model;

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
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                statusOfAllSubtusks = Status.IN_PROGRESS;
            } else if (subtask.getStatus() == Status.DONE) {
                doneCounter++;
            }
        }
        if (doneCounter == epicSubtusks.size()) {
            statusOfAllSubtusks = Status.DONE;
        }
        return statusOfAllSubtusks;
    }
}
