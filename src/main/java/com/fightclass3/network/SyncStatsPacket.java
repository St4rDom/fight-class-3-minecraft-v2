package com.fightclass3.network;

import com.fightclass3.capability.PlayerStatsCapability;
import com.fightclass3.client.ClientStatsCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.List;
import java.util.function.Supplier;

public class SyncStatsPacket {
    public final int strength, vitality, agility;
    public final String title, activeSpeciality;
    public final List<String> unlockedSpecialities;

    public SyncStatsPacket(int str, int vit, int agi, String title,
                           String activeSpec, List<String> unlocked) {
        this.strength = str; this.vitality = vit; this.agility = agi;
        this.title = title; this.activeSpeciality = activeSpec;
        this.unlockedSpecialities = unlocked;
    }

    public static void encode(SyncStatsPacket p, FriendlyByteBuf buf) {
        buf.writeInt(p.strength); buf.writeInt(p.vitality); buf.writeInt(p.agility);
        buf.writeUtf(p.title, 64); buf.writeUtf(p.activeSpeciality, 64);
        buf.writeInt(p.unlockedSpecialities.size());
        for (String s : p.unlockedSpecialities) buf.writeUtf(s, 64);
    }

    public static SyncStatsPacket decode(FriendlyByteBuf buf) {
        int str = buf.readInt(), vit = buf.readInt(), agi = buf.readInt();
        String title = buf.readUtf(64), spec = buf.readUtf(64);
        int n = buf.readInt();
        List<String> list = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) list.add(buf.readUtf(64));
        return new SyncStatsPacket(str, vit, agi, title, spec, list);
    }

    public static void handle(SyncStatsPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientStatsCache.update(pkt));
        ctx.get().setPacketHandled(true);
    }
}
