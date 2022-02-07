package playtime.meter.util.events.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface C2SPlayPacketListener extends ServerPlayPacketListener {
    default void apply(Packet<ServerPlayPacketListener> packet, CallbackInfo info) {
        packet.apply(this);
    }

    @Override
    default void onHandSwing(HandSwingC2SPacket packet) {

    }

    @Override
    default void onChatMessage(ChatMessageC2SPacket packet) {

    }

    @Override
    default void onClientStatus(ClientStatusC2SPacket packet) {

    }

    @Override
    default void onClientSettings(ClientSettingsC2SPacket packet) {

    }

    @Override
    default void onButtonClick(ButtonClickC2SPacket packet) {

    }

    @Override
    default void onClickSlot(ClickSlotC2SPacket packet) {

    }

    @Override
    default void onCraftRequest(CraftRequestC2SPacket packet) {

    }

    @Override
    default void onCloseHandledScreen(CloseHandledScreenC2SPacket packet) {

    }

    @Override
    default void onCustomPayload(CustomPayloadC2SPacket packet) {

    }

    @Override
    default void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet) {

    }

    @Override
    default void onKeepAlive(KeepAliveC2SPacket packet) {

    }

    @Override
    default void onPlayerMove(PlayerMoveC2SPacket packet) {

    }

    @Override
    default void onPong(PlayPongC2SPacket packet) {

    }

    @Override
    default void onUpdatePlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet) {

    }

    @Override
    default void onPlayerAction(PlayerActionC2SPacket packet) {

    }

    @Override
    default void onClientCommand(ClientCommandC2SPacket packet) {

    }

    @Override
    default void onPlayerInput(PlayerInputC2SPacket packet) {

    }

    @Override
    default void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet) {

    }

    @Override
    default void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet) {

    }

    @Override
    default void onUpdateSign(UpdateSignC2SPacket packet) {

    }

    @Override
    default void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet) {

    }

    @Override
    default void onPlayerInteractItem(PlayerInteractItemC2SPacket packet) {

    }

    @Override
    default void onSpectatorTeleport(SpectatorTeleportC2SPacket packet) {

    }

    @Override
    default void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {

    }

    @Override
    default void onBoatPaddleState(BoatPaddleStateC2SPacket packet) {

    }

    @Override
    default void onVehicleMove(VehicleMoveC2SPacket packet) {

    }

    @Override
    default void onTeleportConfirm(TeleportConfirmC2SPacket packet) {

    }

    @Override
    default void onRecipeBookData(RecipeBookDataC2SPacket packet) {

    }

    @Override
    default void onRecipeCategoryOptions(RecipeCategoryOptionsC2SPacket packet) {

    }

    @Override
    default void onAdvancementTab(AdvancementTabC2SPacket packet) {

    }

    @Override
    default void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet) {

    }

    @Override
    default void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet) {

    }

    @Override
    default void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket packet) {

    }

    @Override
    default void onPickFromInventory(PickFromInventoryC2SPacket packet) {

    }

    @Override
    default void onRenameItem(RenameItemC2SPacket packet) {

    }

    @Override
    default void onUpdateBeacon(UpdateBeaconC2SPacket packet) {

    }

    @Override
    default void onUpdateStructureBlock(UpdateStructureBlockC2SPacket packet) {

    }

    @Override
    default void onSelectMerchantTrade(SelectMerchantTradeC2SPacket packet) {

    }

    @Override
    default void onBookUpdate(BookUpdateC2SPacket packet) {

    }

    @Override
    default void onQueryEntityNbt(QueryEntityNbtC2SPacket packet) {

    }

    @Override
    default void onQueryBlockNbt(QueryBlockNbtC2SPacket packet) {

    }

    @Override
    default void onUpdateJigsaw(UpdateJigsawC2SPacket packet) {

    }

    @Override
    default void onJigsawGenerating(JigsawGeneratingC2SPacket packet) {

    }

    @Override
    default void onUpdateDifficulty(UpdateDifficultyC2SPacket packet) {

    }

    @Override
    default void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket packet) {

    }

    @Override
    default void onDisconnected(Text reason) {

    }

    @Override
    default ClientConnection getConnection() {
        throw new IllegalStateException();
    }
}
