package com.fightclass3.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import java.util.*;

public class PlayerStatsData implements IPlayerStats {
    private int    strength  = 0;
    private int    vitality  = 0;
    private int    agility   = 0;
    private String title     = "None";
    private String activeSpeciality = "None";
    private final Set<String> unlockedSpecialities = new HashSet<>();

    @Override public int    getStrength()           { return strength; }
    @Override public void   setStrength(int v)      { strength = Math.max(0, Math.min(100, v)); }
    @Override public int    getVitality()           { return vitality; }
    @Override public void   setVitality(int v)      { vitality = Math.max(0, Math.min(500, v)); }
    @Override public int    getAgility()            { return agility; }
    @Override public void   setAgility(int v)       { agility = Math.max(0, Math.min(100, v)); }
    @Override public String getTitle()              { return title; }
    @Override public void   setTitle(String t)      { title = t == null ? "None" : t; }
    @Override public String getActiveSpeciality()   { return activeSpeciality; }
    @Override public void   setActiveSpeciality(String s) {
        if (s == null || s.equals("None") || unlockedSpecialities.contains(s))
            activeSpeciality = s == null ? "None" : s;
    }
    @Override public Set<String> getUnlockedSpecialities() { return Collections.unmodifiableSet(unlockedSpecialities); }
    @Override public void unlockSpeciality(String s) { unlockedSpecialities.add(s); }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("strength", strength); tag.putInt("vitality", vitality);
        tag.putInt("agility", agility);   tag.putString("title", title);
        tag.putString("activeSpeciality", activeSpeciality);
        ListTag sl = new ListTag();
        for (String s : unlockedSpecialities) sl.add(StringTag.valueOf(s));
        tag.put("specialities", sl);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        strength = tag.getInt("strength"); vitality = tag.getInt("vitality");
        agility  = tag.getInt("agility"); title    = tag.getString("title");
        activeSpeciality = tag.getString("activeSpeciality");
        unlockedSpecialities.clear();
        if (tag.contains("specialities", Tag.TAG_LIST)) {
            ListTag sl = tag.getList("specialities", Tag.TAG_STRING);
            for (int i = 0; i < sl.size(); i++) unlockedSpecialities.add(sl.getString(i));
        }
        if (title.isEmpty()) title = "None";
        if (activeSpeciality.isEmpty()) activeSpeciality = "None";
    }
}
