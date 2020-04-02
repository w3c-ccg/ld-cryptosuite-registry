package canonical;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class Canonical {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private Canonical() {}

    public static String canonicalize(final String json) throws Exception {
       final  JsonElement el = JsonParser.parseString(json);
        if (!el.isJsonObject()) {
            throw new Exception("This only works on JSON Objects.");
        }
        final JsonObject o = el.getAsJsonObject();

        // The tree map will handle the sorting for us
        final TreeMap<String, Object> map = new TreeMap<>();
        final Set<Map.Entry<String, JsonElement>> entrySet = o.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            if (entry.getValue().isJsonObject()) {
                JsonElement sortedObject = sortJSONObject(entry.getValue().getAsJsonObject());
                map.put(entry.getKey(), sortedObject);
            } else if (entry.getValue().isJsonArray()) {
                JsonElement sortedArray = sortJSONArray(entry.getValue().getAsJsonArray());
                map.put(entry.getKey(), sortedArray);
            } else {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return GSON.toJson(map);
    }

    private static JsonElement sortJSONObject(final JsonObject object) {
        final TreeMap<String, Object> map = new TreeMap<>();
        final Set<Map.Entry<String, JsonElement>> entrySet = object.getAsJsonObject().entrySet();
        for (final Map.Entry<String, JsonElement> entry : entrySet) {
            if (entry.getValue().isJsonObject()) {
                JsonElement sortedChild = sortJSONObject(entry.getValue().getAsJsonObject());
                map.put(entry.getKey(), sortedChild);
            } else if (entry.getValue().isJsonArray()) {
                JsonArray array = entry.getValue().getAsJsonArray();
                map.put(entry.getKey(), sortJSONArray(array));
            } else {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return GSON.toJsonTree(map);
    }

    private static JsonArray sortJSONArray(final JsonArray array) {
        final JsonArray outArray = new JsonArray(array.size());
        for (final JsonElement e : array) {
            if (e.isJsonObject()) {
                final JsonElement sortedChild = sortJSONObject(e.getAsJsonObject());
                outArray.add(GSON.toJsonTree(sortedChild));
            } else {
                outArray.add(e);
            }
        }
        return outArray;
    }

    public static String toJson(final Object o) {
        return GSON.toJson(o);
    }
}