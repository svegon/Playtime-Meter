package playtime.meter.util;

import com.google.gson.JsonObject;
import playtime.meter.Main;
import playtime.meter.util.stat.PlaytimeMeter;

import java.io.IOException;

public final class ServerSettings {
    private int aFKTimeout = PlaytimeMeter.getDefaultAFKTimeout();

    public ServerSettings() {
        load();
    }

    public int setAFKTimeout(int afkTimeout) {
        int ret = aFKTimeout;
        aFKTimeout = Math.max(afkTimeout, 0);
        save();
        return ret;
    }

    public int getAFKTimeout() {
        return aFKTimeout;
    }

    public void load() {
        try {
            JsonUtil.parseFileToObject(Main.MOD_DIRECTORY.resolve("settings.json"))
                    .flatMap(json -> JsonUtil.getProperty(json, "afk_timeout"))
                    .ifPresent(optional -> JsonUtil.getAsInt(optional).ifPresent(i -> aFKTimeout = i));
        } catch (IOException ignore) {

        }

        save();
    }

    public void save() {
        JsonObject json = new JsonObject();

        json.addProperty("afk_timeout", getAFKTimeout());

        try {
            JsonUtil.saveToFile(json, Main.MOD_DIRECTORY.resolve("settings.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
