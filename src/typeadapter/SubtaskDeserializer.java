package typeadapter;

import com.google.gson.*;
import model.Subtask;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskDeserializer implements JsonDeserializer<Subtask> {
    @Override
    public Subtask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        int subtasksEpicId = jsonObject.get("subtasksEpicId").getAsInt();
        Duration duration = context.deserialize(jsonObject.get("duration"), Duration.class);
        LocalDateTime startTime = context.deserialize(jsonObject.get("startTime"), LocalDateTime.class);
        Subtask subtask = new Subtask(name, description, subtasksEpicId, duration, startTime);
        return subtask;
    }
}
