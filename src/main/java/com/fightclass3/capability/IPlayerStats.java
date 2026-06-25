package com.fightclass3.capability;

import net.minecraft.nbt.CompoundTag;
import java.util.Set;

public interface IPlayerStats {
    int getStrength();   void setStrength(int v);
    int getVitality();   void setVitality(int v);
    int getAgility();    void setAgility(int v);
    String getTitle();   void setTitle(String t);
    String getActiveSpeciality(); void setActiveSpeciality(String s);
    Set<String> getUnlockedSpecialities();
    void unlockSpeciality(String s);
    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag tag);
}
