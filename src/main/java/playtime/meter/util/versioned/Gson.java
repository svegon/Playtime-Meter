package playtime.meter.util.versioned;

import com.google.gson.JsonElement;

import java.io.Reader;

public interface Gson {
    JsonElement parseReader(Reader reader);
}
