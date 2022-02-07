package playtime.meter;

import playtime.meter.util.ServerSettings;

public final class ServerMain {
    private static final ServerSettings SETTINGS = new ServerSettings();

    private ServerMain() {
        throw new UnsupportedOperationException();
    }

    public static void init() {

    }

    public static ServerSettings getSettings() {
        return SETTINGS;
    }
}
