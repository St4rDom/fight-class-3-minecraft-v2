package com.fightclass3.event;

import com.fightclass3.FightClass3Mod;
import com.fightclass3.capability.*;
import com.fightclass3.entity.JiuJiTaeEntity;
import com.fightclass3.network.PacketHandler;
import com.fightclass3.network.SyncStatsPacket;
import com.fightclass3.registry.EntityRegistry;
import com.fightclass3.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.ChatFormatting;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import java.util.*;

@Mod.EventBusSubscriber(modid = FightClass3Mod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEventHandler {

    private static final UUID STRENGTH_MOD_UUID = UUID.fromString("a1b2c3d4-1111-2222-3333-aabbccddeeff");
    private static final UUID SPEED_MOD_UUID    = UUID.fromString("b2c3d4e5-2222-3333-4444-aabbccddeeff");
    private static final UUID HEALTH_MOD_UUID   = UUID.fromString("c3d4e5f6-3333-4444-5555-aabbccddeeff");

    // ── Attach stats capability to players ────────────────────────────────────
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;
        PlayerStatsProvider p = new PlayerStatsProvider();
        event.addCapability(new ResourceLocation(FightClass3Mod.MOD_ID, "player_stats"), p);
        event.addListener(p::invalidate);
    }

    // ── Sync stats to player on login ─────────────────────────────────────────
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            syncStatsToPlayer(sp);
            // Give punch item if they don't have it
            ensurePunchItem(sp);
        }
    }

    // ── Restore punch item on respawn ─────────────────────────────────────────
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            syncStatsToPlayer(sp);
            ensurePunchItem(sp);
        }
    }

    // ── Copy stats on dimension change ────────────────────────────────────────
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) syncStatsToPlayer(sp);
    }

    // ── Player tick: apply stat effects + speciality ──────────────────────────
    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.tickCount % 20 != 0) return;

        player.getCapability(PlayerStatsCapability.INSTANCE).ifPresent(stats -> {
            applyVitality(player, stats);
            applyStrengthSpeed(player, stats);
            applySpecialityEffects(player, stats);
        });
    }

    private static void applyVitality(ServerPlayer player, IPlayerStats stats) {
        int extraHearts = stats.getVitality() / 10;
        double bonusHp  = extraHearts * 2.0;
        var hpAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttr == null) return;
        hpAttr.removeModifier(HEALTH_MOD_UUID);
        if (bonusHp > 0) hpAttr.addTransientModifier(
                new AttributeModifier(HEALTH_MOD_UUID, "fct_vitality", bonusHp, AttributeModifier.Operation.ADDITION));
    }

    private static void applyStrengthSpeed(ServerPlayer player, IPlayerStats stats) {
        var atkAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        var spdAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (atkAttr != null) {
            atkAttr.removeModifier(STRENGTH_MOD_UUID);
            if (stats.getStrength() > 0)
                atkAttr.addTransientModifier(new AttributeModifier(STRENGTH_MOD_UUID,
                        "fct_strength", stats.getStrength() * 0.1, AttributeModifier.Operation.ADDITION));
        }
        if (spdAttr != null) {
            spdAttr.removeModifier(SPEED_MOD_UUID);
            if (stats.getAgility() > 0)
                spdAttr.addTransientModifier(new AttributeModifier(SPEED_MOD_UUID,
                        "fct_agility", stats.getAgility() * 0.002, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void applySpecialityEffects(ServerPlayer player, IPlayerStats stats) {
        String spec = stats.getActiveSpeciality();
        var level = player.level();
        boolean isDay = level.getDayTime() % 24000L < 12000L;

        if (spec.equals("Insanity")) {
            // Permanent combat buffs
            give(player, MobEffects.DAMAGE_BOOST,    2);  // Strength 3
            give(player, MobEffects.REGENERATION,    2);  // Regen 3
            give(player, MobEffects.ABSORPTION,      1);  // Absorption 2
            give(player, MobEffects.HEALTH_BOOST,    4);  // Health Boost 5
            give(player, MobEffects.DIG_SPEED,       2);  // Haste 3

            if (isDay) {
                // Darkness + red mob outlines
                give(player, MobEffects.DARKNESS, 3);
                applyRedOutlines(player, 50.0);
            } else {
                // Night vision, clear the darkness
                give(player, MobEffects.NIGHT_VISION, 0);
                player.removeEffect(MobEffects.DARKNESS);
                clearRedOutlines(player);
            }
        } else if (spec.equals("PainTolerance")) {
            give(player, MobEffects.DAMAGE_RESISTANCE, 4);  // Resistance 5
            give(player, MobEffects.FIRE_RESISTANCE,   2);  // Fire Resistance 3
            give(player, MobEffects.REGENERATION,      2);  // Regen 3
            give(player, MobEffects.ABSORPTION,        1);  // Absorption 2
            give(player, MobEffects.HEALTH_BOOST,      4);  // Health Boost 5
        }
    }

    private static void give(Player player, net.minecraft.world.effect.MobEffect effect, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, 40, amplifier, false, false));
    }

    private static void applyRedOutlines(ServerPlayer player, double radius) {
        if (!(player.level() instanceof ServerLevel sl)) return;
        Scoreboard sc = sl.getScoreboard();
        PlayerTeam team = sc.getPlayerTeam("fct_insanity");
        if (team == null) {
            team = sc.addPlayerTeam("fct_insanity");
            team.setColor(ChatFormatting.RED);
        }
        final PlayerTeam finalTeam = team;
        // Clear and repopulate
        new HashSet<>(finalTeam.getPlayers()).forEach(name -> sc.removePlayerFromTeam(name, finalTeam));
        sl.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius), e -> !(e instanceof Player))
          .forEach(mob -> { mob.setGlowingTag(true); sc.addPlayerToTeam(mob.getStringUUID(), finalTeam); });
    }

    private static void clearRedOutlines(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel sl)) return;
        Scoreboard sc = sl.getScoreboard();
        PlayerTeam team = sc.getPlayerTeam("fct_insanity");
        if (team == null) return;
        new HashSet<>(team.getPlayers()).forEach(name -> {
            Entity e = sl.getEntity(UUID.fromString(name.replace("-","").replaceAll("(........)(....)(....)(....)(............)", "$1-$2-$3-$4-$5")));
            if (e != null) e.setGlowingTag(false);
            sc.removePlayerFromTeam(name, team);
        });
    }

    // ── JJT Death → achievements + drops ─────────────────────────────────────
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof JiuJiTaeEntity jjt)) return;
        DamageSource src = event.getSource();
        if (!(src.getEntity() instanceof ServerPlayer player)) return;

        // Award I'M FREE advancement
        awardAdvancement(player, "imfree");

        // Grant Psychopath title
        player.getCapability(PlayerStatsCapability.INSTANCE).ifPresent(stats -> {
            stats.setTitle("Psychopath");
            syncStatsToPlayer(player);
        });

        player.sendSystemMessage(Component.literal("\u00a76[FCT] \u00a7fTitle unlocked: \u00a7cPsychopath"));

        // 20% chance: drop Recollection
        if (player.level().random.nextFloat() < 0.20f) {
            jjt.spawnAtLocation(new ItemStack(com.fightclass3.registry.ItemRegistry.RECOLLECTION.get()));
        }

        // Castle of Fortitude: killed JJT with \u22644 hearts
        if (player.getHealth() <= 8.0f) {
            awardAdvancement(player, "castle_of_fortitude");
            // Drop Willpower (guaranteed for this achievement)
            jjt.spawnAtLocation(new ItemStack(com.fightclass3.registry.ItemRegistry.WILLPOWER.get()));
            player.sendSystemMessage(Component.literal("\u00a75[FCT] \u00a7fAchievement: Castle of Fortitude!"));
        }
    }

    private static void awardAdvancement(ServerPlayer player, String id) {
        var server = player.server;
        if (server == null) return;
        var adv = server.getAdvancements().getAdvancement(
                new ResourceLocation(FightClass3Mod.MOD_ID, id));
        if (adv == null) return;
        var progress = player.getAdvancements().getOrStartProgress(adv);
        if (!progress.isDone())
            adv.getCriteria().keySet().forEach(c -> player.getAdvancements().award(adv, c));
    }

    // ── Punch attack damage = wooden sword (4 dmg) ────────────────────────────
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (!(player.getMainHandItem().getItem() instanceof com.fightclass3.items.PunchItem)) return;
        // Wooden sword = 4 damage base. Attribute modifiers (strength stat) will add on top.
        // The item itself has no weapon damage so we just let the base attack go through.
        // Strength stat was added as attribute modifier so it's already counted.
    }

    // ── Punch item: give to player on join ────────────────────────────────────
    public static void ensurePunchItem(ServerPlayer player) {
        boolean hasPunch = false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).getItem() instanceof com.fightclass3.items.PunchItem) {
                hasPunch = true; break;
            }
        }
        if (!hasPunch) {
            ItemStack punch = new ItemStack(ItemRegistry.PUNCH.get());
            player.getInventory().setItem(0, punch); // hotbar slot 0
        }
    }

    // ── Utility: sync stats to client ─────────────────────────────────────────
    public static void syncStatsToPlayer(ServerPlayer player) {
        player.getCapability(PlayerStatsCapability.INSTANCE).ifPresent(stats -> {
            PacketHandler.sendToPlayer(new SyncStatsPacket(
                    stats.getStrength(), stats.getVitality(), stats.getAgility(),
                    stats.getTitle(), stats.getActiveSpeciality(),
                    new ArrayList<>(stats.getUnlockedSpecialities())
            ), player);
        });
    }
}
