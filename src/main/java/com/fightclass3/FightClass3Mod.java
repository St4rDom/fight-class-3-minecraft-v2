package com.fightclass3;

import com.fightclass3.capability.PlayerStatsCapability;
import com.fightclass3.client.ClientEventHandler;
import com.fightclass3.entity.JiuJiTaeEntity;
import com.fightclass3.network.PacketHandler;
import com.fightclass3.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(FightClass3Mod.MOD_ID)
public class FightClass3Mod {
    public static final String MOD_ID = "fightclass3";

    public FightClass3Mod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(PlayerStatsCapability::register);
        ItemRegistry.ITEMS.register(modBus);
        EntityRegistry.ENTITIES.register(modBus);
        CreativeTabRegistry.TABS.register(modBus);
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::registerAttributes);
        modBus.addListener(this::registerSpawnPlacements);
        if (FMLEnvironment.dist == Dist.CLIENT)
            modBus.addListener(this::clientSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::register);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ClientEventHandler::registerAnimations);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.JIU_JI_TAE.get(), JiuJiTaeEntity.createAttributes().build());
    }

    private void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(EntityRegistry.JIU_JI_TAE.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
    }
}
