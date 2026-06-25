package com.fightclass3.client;

import com.fightclass3.network.SyncStatsPacket;
import java.util.*;

/** Client-side mirror of the player's FCT stats. Updated by SyncStatsPacket. */
public class ClientStatsCache {
    public static int    strength = 0, vitality = 0, agility = 0;
    public static String title = "None", activeSpeciality = "None";
    public static List<String> unlockedSpecialities = new ArrayList<>();

    public static void update(SyncStatsPacket pkt) {
        strength = pkt.strength; vitality = pkt.vitality; agility = pkt.agility;
        title = pkt.title; activeSpeciality = pkt.activeSpeciality;
        unlockedSpecialities = new ArrayList<>(pkt.unlockedSpecialities);
    }
}
