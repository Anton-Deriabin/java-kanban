package model;

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
}
