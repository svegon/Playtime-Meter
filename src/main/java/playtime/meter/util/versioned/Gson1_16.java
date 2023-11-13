package playtime.meter.util.versioned;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.Reader;

class Gson1_16 implements Gson {
    Gson1_16() {
    }

    public JsonElement parseReader(Reader reader) {
        return new JsonParser().parse(reader);
    }
}
