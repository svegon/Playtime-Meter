package playtime.meter.mixinterfaces;

import net.fabricmc.fabric.api.event.Event;
import playtime.meter.util.events.network.C2SPlayPacketListener;
import playtime.meter.util.events.network.S2CPlayPacketListener;
import playtime.meter.util.stat.ServerPlaytimeMeter;

public interface IServerPlayerEntity {
    ServerPlaytimeMeter getPlaytimeMeter();

    Event<C2SPlayPacketListener> packetReceivedEvent();

    Event<S2CPlayPacketListener> packetSentEvent();
}
