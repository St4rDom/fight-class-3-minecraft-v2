package com.fightclass3.network;

import com.fightclass3.FightClass3Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.Optional;

public class PacketHandler {
    private static final String PROTOCOL = "1";
    private static int id = 0;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(FightClass3Mod.MOD_ID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        CHANNEL.registerMessage(id++, SyncStatsPacket.class,
                SyncStatsPacket::encode, SyncStatsPacket::decode, SyncStatsPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, SetStatPacket.class,
                SetStatPacket::encode, SetStatPacket::decode, SetStatPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void sendToPlayer(Object pkt, ServerPlayer p) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), pkt);
    }
    public static void sendToServer(Object pkt) { CHANNEL.sendToServer(pkt); }
}
