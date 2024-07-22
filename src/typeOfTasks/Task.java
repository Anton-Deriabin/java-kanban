package typeOfTasks;

import status.Status;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;
    private static int nextId = 1;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = nextId;
        this.status = Status.NEW;
        nextId++;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.id = nextId;
        this.status = status;
        nextId++;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public String toString() {
        return "typeOfTasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
