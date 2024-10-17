package typeadapter;

import com.google.gson.*;
import model.Epic;
import model.Subtask;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EpicDeserializer implements JsonDeserializer<Epic> {
    @Override
    public Epic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        Duration duration = context.deserialize(jsonObject.get("duration"), Duration.class);
        LocalDateTime startTime = context.deserialize(jsonObject.get("startTime"), LocalDateTime.class);
        HashMap<Integer, Subtask> epicSubtasks = new HashMap<>();
        JsonObject subtasksJson = jsonObject.getAsJsonObject("epicSubtasks");
        for (Map.Entry<String, JsonElement> entry : subtasksJson.entrySet()) {
            Integer subtaskId = Integer.parseInt(entry.getKey());
            Subtask subtask = context.deserialize(entry.getValue(), Subtask.class);
            epicSubtasks.put(subtaskId, subtask);
        }
        return new Epic(name, description, duration, startTime, epicSubtasks);
    }
}


