/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.ping.ServerPingPacketListener;

public interface ServerGamePacketListener
    extends ServerCommonPacketListener, ServerPingPacketListener {
  @Override
  public default ConnectionProtocol protocol() {
    return ConnectionProtocol.PLAY;
  }

  public void handleAnimate(ServerboundSwingPacket var1);

  public void handleChat(ServerboundChatPacket var1);

  public void handleChatCommand(ServerboundChatCommandPacket var1);

  public void handleSignedChatCommand(ServerboundChatCommandSignedPacket var1);

  public void handleChatAck(ServerboundChatAckPacket var1);

  public void handleClientCommand(ServerboundClientCommandPacket var1);

  public void handleContainerButtonClick(ServerboundContainerButtonClickPacket var1);

  public void handleContainerClick(ServerboundContainerClickPacket var1);

  public void handlePlaceRecipe(ServerboundPlaceRecipePacket var1);

  public void handleContainerClose(ServerboundContainerClosePacket var1);

  public void handleAttack(ServerboundAttackPacket var1);

  public void handleInteract(ServerboundInteractPacket var1);

  public void handleSpectateEntity(ServerboundSpectateEntityPacket var1);

  public void handleMovePlayer(ServerboundMovePlayerPacket var1);

  public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket var1);

  public void handlePlayerAction(ServerboundPlayerActionPacket var1);

  public void handlePlayerCommand(ServerboundPlayerCommandPacket var1);

  public void handlePlayerInput(ServerboundPlayerInputPacket var1);

  public void handleSetCarriedItem(ServerboundSetCarriedItemPacket var1);

  public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket var1);

  public void handleSignUpdate(ServerboundSignUpdatePacket var1);

  public void handleUseItemOn(ServerboundUseItemOnPacket var1);

  public void handleUseItem(ServerboundUseItemPacket var1);

  public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket var1);

  public void handlePaddleBoat(ServerboundPaddleBoatPacket var1);

  public void handleMoveVehicle(ServerboundMoveVehiclePacket var1);

  public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket var1);

  public void handleAcceptPlayerLoad(ServerboundPlayerLoadedPacket var1);

  public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket var1);

  public void handleBundleItemSelectedPacket(ServerboundSelectBundleItemPacket var1);

  public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket var1);

  public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket var1);

  public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket var1);

  public void handleSetCommandBlock(ServerboundSetCommandBlockPacket var1);

  public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket var1);

  public void handlePickItemFromBlock(ServerboundPickItemFromBlockPacket var1);

  public void handlePickItemFromEntity(ServerboundPickItemFromEntityPacket var1);

  public void handleRenameItem(ServerboundRenameItemPacket var1);

  public void handleSetBeaconPacket(ServerboundSetBeaconPacket var1);

  public void handleSetGameRule(ServerboundSetGameRulePacket var1);

  public void handleSetStructureBlock(ServerboundSetStructureBlockPacket var1);

  public void handleSetTestBlock(ServerboundSetTestBlockPacket var1);

  public void handleTestInstanceBlockAction(ServerboundTestInstanceBlockActionPacket var1);

  public void handleSelectTrade(ServerboundSelectTradePacket var1);

  public void handleEditBook(ServerboundEditBookPacket var1);

  public void handleEntityTagQuery(ServerboundEntityTagQueryPacket var1);

  public void handleContainerSlotStateChanged(ServerboundContainerSlotStateChangedPacket var1);

  public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQueryPacket var1);

  public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket var1);

  public void handleJigsawGenerate(ServerboundJigsawGeneratePacket var1);

  public void handleChangeDifficulty(ServerboundChangeDifficultyPacket var1);

  public void handleChangeGameMode(ServerboundChangeGameModePacket var1);

  public void handleLockDifficulty(ServerboundLockDifficultyPacket var1);

  public void handleChatSessionUpdate(ServerboundChatSessionUpdatePacket var1);

  public void handleConfigurationAcknowledged(ServerboundConfigurationAcknowledgedPacket var1);

  public void handleChunkBatchReceived(ServerboundChunkBatchReceivedPacket var1);

  public void handleDebugSubscriptionRequest(ServerboundDebugSubscriptionRequestPacket var1);

  public void handleClientTickEnd(ServerboundClientTickEndPacket var1);
}
