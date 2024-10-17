package typeadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import model.Epic;
import model.Subtask;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class EpicSerializer implements JsonSerializer<Epic> {
    @Override
    public JsonElement serialize(Epic epic, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonEpic = new JsonObject();
        jsonEpic.addProperty("name", epic.getName());
        jsonEpic.addProperty("description", epic.getDescription());
        jsonEpic.add("duration", context.serialize(epic.getDuration(), Duration.class));
        jsonEpic.add("startTime", context.serialize(epic.getStartTime(), LocalDateTime.class));
        JsonObject epicSubtasksJson = new JsonObject();
        for (Map.Entry<Integer, Subtask> entry : epic.getEpicSubtusks().entrySet()) {
            epicSubtasksJson.add(entry.getKey().toString(), context.serialize(entry.getValue(), Subtask.class));
        }
        jsonEpic.add("epicSubtasks", epicSubtasksJson);
        return jsonEpic;
    }
}


