import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;
    private static int taskCounter;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = hashCode();
        this.status = Status.NEW;
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

    public static int getTaskCounter() {
        return taskCounter;
    }

    public static void setTaskCounter() {
        Task.taskCounter++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null) {
            hash = name.hashCode();
        }
        if (description != null) {
            hash = hash + description.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
