package typeadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import model.Task;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskSerializer implements JsonSerializer<Task> {
    @Override
    public JsonElement serialize(Task task, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonTask = new JsonObject();
        jsonTask.addProperty("name", task.getName());
        jsonTask.addProperty("description", task.getDescription());
        jsonTask.add("duration", context.serialize(task.getDuration(), Duration.class));
        jsonTask.add("startTime", context.serialize(task.getStartTime(), LocalDateTime.class));
        return jsonTask;
    }
}

