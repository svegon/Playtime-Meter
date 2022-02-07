package playtime.meter.mixinterfaces;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.util.math.Vec3d;
import playtime.meter.util.events.network.C2SPlayPacketListener;
import playtime.meter.util.events.network.S2CPlayPacketListener;

public interface IServerPlayNetworkHandler {
    int ticks();

    Vec3d lastTickPos();
}
