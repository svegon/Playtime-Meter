package playtime.meter.util.versioned;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.Reader;

final class Gson1_18 implements Gson {
    Gson1_18() {
    }

    public JsonElement parseReader(Reader reader) {
        return JsonParser.parseReader(reader);
    }
}
