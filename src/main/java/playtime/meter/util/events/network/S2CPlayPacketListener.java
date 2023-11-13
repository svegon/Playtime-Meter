package playtime.meter.util.events.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface S2CPlayPacketListener extends ClientPlayPacketListener {
    default void apply(Packet<ClientPlayPacketListener> packet, CallbackInfo info) {
        packet.apply(this);
    }

    @Override
    default void onEntitySpawn(EntitySpawnS2CPacket packet) {

    }

    @Override
    default void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket packet) {

    }

    @Override
    default void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet) {

    }

    @Override
    default void onPlayerSpawn(PlayerSpawnS2CPacket packet) {

    }

    @Override
    default void onEntityAnimation(EntityAnimationS2CPacket packet) {

    }

    @Override
    default void onStatistics(StatisticsS2CPacket packet) {

    }

    @Override
    default void onUnlockRecipes(UnlockRecipesS2CPacket packet) {

    }

    @Override
    default void onBlockBreakingProgress(BlockBreakingProgressS2CPacket packet) {

    }

    @Override
    default void onSignEditorOpen(SignEditorOpenS2CPacket packet) {

    }

    @Override
    default void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {

    }

    @Override
    default void onBlockEvent(BlockEventS2CPacket packet) {

    }

    @Override
    default void onBlockUpdate(BlockUpdateS2CPacket packet) {

    }

    @Override
    default void onGameMessage(GameMessageS2CPacket packet) {

    }

    @Override
    default void onChatMessage(ChatMessageS2CPacket packet) {

    }

    @Override
    default void onChatPreview(ChatPreviewS2CPacket packet) {

    }

    @Override
    default void onChatPreviewStateChange(ChatPreviewStateChangeS2CPacket packet) {

    }

    @Override
    default void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {

    }

    @Override
    default void onMapUpdate(MapUpdateS2CPacket packet) {

    }

    @Override
    default void onCloseScreen(CloseScreenS2CPacket packet) {

    }

    @Override
    default void onInventory(InventoryS2CPacket packet) {

    }

    @Override
    default void onOpenHorseScreen(OpenHorseScreenS2CPacket packet) {

    }

    @Override
    default void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet) {

    }

    @Override
    default void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet) {

    }

    @Override
    default void onCustomPayload(CustomPayloadS2CPacket packet) {

    }

    @Override
    default void onDisconnect(DisconnectS2CPacket packet) {

    }

    @Override
    default void onEntityStatus(EntityStatusS2CPacket packet) {

    }

    @Override
    default void onEntityAttach(EntityAttachS2CPacket packet) {

    }

    @Override
    default void onEntityPassengersSet(EntityPassengersSetS2CPacket packet) {

    }

    @Override
    default void onExplosion(ExplosionS2CPacket packet) {

    }

    @Override
    default void onGameStateChange(GameStateChangeS2CPacket packet) {

    }

    @Override
    default void onKeepAlive(KeepAliveS2CPacket packet) {

    }

    @Override
    default void onChunkData(ChunkDataS2CPacket packet) {

    }

    @Override
    default void onUnloadChunk(UnloadChunkS2CPacket packet) {

    }

    @Override
    default void onWorldEvent(WorldEventS2CPacket packet) {

    }

    @Override
    default void onGameJoin(GameJoinS2CPacket packet) {

    }

    @Override
    default void onEntity(EntityS2CPacket packet) {

    }

    @Override
    default void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {

    }

    @Override
    default void onParticle(ParticleS2CPacket packet) {

    }

    @Override
    default void onPing(PlayPingS2CPacket packet) {

    }

    @Override
    default void onPlayerAbilities(PlayerAbilitiesS2CPacket packet) {

    }

    @Override
    default void onPlayerList(PlayerListS2CPacket packet) {

    }

    @Override
    default void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {

    }

    @Override
    default void onRemoveEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet) {

    }

    @Override
    default void onPlayerRespawn(PlayerRespawnS2CPacket packet) {

    }

    @Override
    default void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {

    }

    @Override
    default void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet) {

    }

    @Override
    default void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {

    }

    @Override
    default void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {

    }

    @Override
    default void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {

    }

    @Override
    default void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {

    }

    @Override
    default void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet) {

    }

    @Override
    default void onHealthUpdate(HealthUpdateS2CPacket packet) {

    }

    @Override
    default void onTeam(TeamS2CPacket packet) {

    }

    @Override
    default void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket packet) {

    }

    @Override
    default void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet) {

    }

    @Override
    default void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {

    }

    @Override
    default void onPlaySound(PlaySoundS2CPacket packet) {

    }

    @Override
    default void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet) {

    }

    @Override
    default void onPlaySoundId(PlaySoundIdS2CPacket packet) {

    }

    @Override
    default void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet) {

    }

    @Override
    default void onEntityPosition(EntityPositionS2CPacket packet) {

    }

    @Override
    default void onEntityAttributes(EntityAttributesS2CPacket packet) {

    }

    @Override
    default void onEntityStatusEffect(EntityStatusEffectS2CPacket packet) {

    }

    @Override
    default void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {

    }

    @Override
    default void onEndCombat(EndCombatS2CPacket packet) {

    }

    @Override
    default void onEnterCombat(EnterCombatS2CPacket packet) {

    }

    @Override
    default void onDeathMessage(DeathMessageS2CPacket packet) {

    }

    @Override
    default void onDifficulty(DifficultyS2CPacket packet) {

    }

    @Override
    default void onSetCameraEntity(SetCameraEntityS2CPacket packet) {

    }

    @Override
    default void onWorldBorderInitialize(WorldBorderInitializeS2CPacket packet) {

    }

    @Override
    default void onWorldBorderInterpolateSize(WorldBorderInterpolateSizeS2CPacket packet) {

    }

    @Override
    default void onWorldBorderSizeChanged(WorldBorderSizeChangedS2CPacket packet) {

    }

    @Override
    default void onWorldBorderWarningTimeChanged(WorldBorderWarningTimeChangedS2CPacket packet) {

    }

    @Override
    default void onWorldBorderWarningBlocksChanged(WorldBorderWarningBlocksChangedS2CPacket packet) {

    }

    @Override
    default void onWorldBorderCenterChanged(WorldBorderCenterChangedS2CPacket packet) {

    }

    @Override
    default void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {

    }

    @Override
    default void onResourcePackSend(ResourcePackSendS2CPacket packet) {

    }

    @Override
    default void onBossBar(BossBarS2CPacket packet) {

    }

    @Override
    default void onCooldownUpdate(CooldownUpdateS2CPacket packet) {

    }

    @Override
    default void onVehicleMove(VehicleMoveS2CPacket packet) {

    }

    @Override
    default void onAdvancements(AdvancementUpdateS2CPacket packet) {

    }

    @Override
    default void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet) {

    }

    @Override
    default void onCraftFailedResponse(CraftFailedResponseS2CPacket packet) {

    }

    @Override
    default void onCommandTree(CommandTreeS2CPacket packet) {

    }

    @Override
    default void onStopSound(StopSoundS2CPacket packet) {

    }

    @Override
    default void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {

    }

    @Override
    default void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet) {

    }

    @Override
    default void onLookAt(LookAtS2CPacket packet) {

    }

    @Override
    default void onNbtQueryResponse(NbtQueryResponseS2CPacket packet) {

    }

    @Override
    default void onLightUpdate(LightUpdateS2CPacket packet) {

    }

    @Override
    default void onOpenWrittenBook(OpenWrittenBookS2CPacket packet) {

    }

    @Override
    default void onOpenScreen(OpenScreenS2CPacket packet) {

    }

    @Override
    default void onSetTradeOffers(SetTradeOffersS2CPacket packet) {

    }

    @Override
    default void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet) {

    }

    @Override
    default void onSimulationDistance(SimulationDistanceS2CPacket packet) {

    }

    @Override
    default void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {

    }

    @Override
    default void onPlayerActionResponse(PlayerActionResponseS2CPacket packet) {

    }

    @Override
    default void onOverlayMessage(OverlayMessageS2CPacket packet) {

    }

    @Override
    default void onSubtitle(SubtitleS2CPacket packet) {

    }

    @Override
    default void onTitle(TitleS2CPacket packet) {

    }

    @Override
    default void onTitleFade(TitleFadeS2CPacket packet) {

    }

    @Override
    default void onTitleClear(ClearTitleS2CPacket packet) {

    }

    @Override
    default void onServerMetadata(ServerMetadataS2CPacket packet) {

    }

    @Override
    default void onDisconnected(Text reason) {

    }

    @Override
    default ClientConnection getConnection() {
        return null;
    }
}
