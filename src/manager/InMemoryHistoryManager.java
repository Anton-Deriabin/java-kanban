package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final ArrayList<Task> watchHistory = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (task != null) {
            if (watchHistory.size() < 10) {
                watchHistory.add(task);
            } else {
                watchHistory.remove(0);
                watchHistory.add(task);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return watchHistory;
    }
}
