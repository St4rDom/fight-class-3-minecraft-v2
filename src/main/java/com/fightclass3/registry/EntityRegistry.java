package com.fightclass3.registry;

import com.fightclass3.FightClass3Mod;
import com.fightclass3.entity.JiuJiTaeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, FightClass3Mod.MOD_ID);

    public static final RegistryObject<EntityType<JiuJiTaeEntity>> JIU_JI_TAE =
            ENTITIES.register("jiu_ji_tae",
                    () -> EntityType.Builder.<JiuJiTaeEntity>of(JiuJiTaeEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(48)
                            .build("fightclass3:jiu_ji_tae"));
}
