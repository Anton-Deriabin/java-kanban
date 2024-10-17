package typeadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import model.Subtask;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskSerializer implements JsonSerializer<Subtask> {
    @Override
    public JsonElement serialize(Subtask subtask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonSubtask = new JsonObject();
        jsonSubtask.addProperty("name", subtask.getName());
        jsonSubtask.addProperty("description", subtask.getDescription());
        jsonSubtask.addProperty("subtasksEpicId", subtask.getSubtasksEpicId());
        jsonSubtask.add("duration", context.serialize(subtask.getDuration(), Duration.class));
        jsonSubtask.add("startTime", context.serialize(subtask.getStartTime(), LocalDateTime.class));
        return jsonSubtask;
    }
}