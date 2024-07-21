import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    ArrayList<Subtask> epicSubtusks;

    public Epic(String name, String description) {
        super(name, description);
        this.epicSubtusks = new ArrayList<>();
    }
}
