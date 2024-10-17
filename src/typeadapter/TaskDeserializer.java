package typeadapter;

import com.google.gson.*;
import model.Task;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        Duration duration = context.deserialize(jsonObject.get("duration"), Duration.class);
        LocalDateTime startTime = context.deserialize(jsonObject.get("startTime"), LocalDateTime.class);
        Task task = new Task(name, description, duration, startTime);
        return task;
    }
}

