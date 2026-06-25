package com.fightclass3.registry;

import com.fightclass3.FightClass3Mod;
import com.fightclass3.items.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, FightClass3Mod.MOD_ID);

    public static final RegistryObject<PunchItem>       PUNCH       = ITEMS.register("punch",       PunchItem::new);
    public static final RegistryObject<RecollectionItem> RECOLLECTION = ITEMS.register("recollection", RecollectionItem::new);
    public static final RegistryObject<WillpowerItem>   WILLPOWER   = ITEMS.register("willpower",   WillpowerItem::new);
}
