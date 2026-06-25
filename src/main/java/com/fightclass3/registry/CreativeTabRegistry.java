package com.fightclass3.registry;

import com.fightclass3.FightClass3Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.core.registries.Registries;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FightClass3Mod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> FCT_TAB =
            TABS.register("fct_tab", () -> CreativeModeTab.builder()
                    .title(Component.literal("Fight Class 3"))
                    .icon(() -> new ItemStack(ItemRegistry.PUNCH.get()))
                    .displayItems((params, output) -> {
                        output.accept(ItemRegistry.PUNCH.get());
                        output.accept(ItemRegistry.RECOLLECTION.get());
                        output.accept(ItemRegistry.WILLPOWER.get());
                    })
                    .build());
}
