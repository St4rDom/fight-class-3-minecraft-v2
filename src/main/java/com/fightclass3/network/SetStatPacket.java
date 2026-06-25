package com.fightclass3.network;

import com.fightclass3.capability.PlayerStatsCapability;
import com.fightclass3.event.PlayerEventHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class SetStatPacket {
    public enum Stat { ACTIVE_SPEC }
    public final Stat stat;
    public final String value;

    public SetStatPacket(Stat stat, String value) { this.stat = stat; this.value = value; }

    public static void encode(SetStatPacket p, FriendlyByteBuf buf) {
        buf.writeEnum(p.stat); buf.writeUtf(p.value, 64);
    }
    public static SetStatPacket decode(FriendlyByteBuf buf) {
        return new SetStatPacket(buf.readEnum(Stat.class), buf.readUtf(64));
    }
    public static void handle(SetStatPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            player.getCapability(PlayerStatsCapability.INSTANCE).ifPresent(stats -> {
                if (pkt.stat == Stat.ACTIVE_SPEC) stats.setActiveSpeciality(pkt.value);
                PlayerEventHandler.syncStatsToPlayer(player);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
