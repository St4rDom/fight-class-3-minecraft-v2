package com.fightclass3.entity;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.animal.Animal;

public class JiuJiTaeEntity extends Monster {

    public JiuJiTaeEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setCustomName(net.minecraft.network.chat.Component.literal("Jiu Ji-Tae"));
        this.setCustomNameVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH,       80.0)  // 40 hearts
                .add(Attributes.MOVEMENT_SPEED,    0.38)  // Superhuman but not insane
                .add(Attributes.ATTACK_DAMAGE,     9.0)   // Hits hard
                .add(Attributes.FOLLOW_RANGE,      35.0)  // Detects from 35 blocks
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected void registerGoals() {
        // Basic survival
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        // Target EVERYTHING within range — players first, then all other living entities
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this, Mob.class, 0, false, false,
                entity -> !(entity instanceof JiuJiTaeEntity) && entity.isAlive()));
    }

    /** JJT never burns (he's a human, not undead, but make him immune to environment) */
    @Override public boolean fireImmune() { return false; }

    @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() {
        return net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_STRONG;
    }
    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource src) {
        return net.minecraft.sounds.SoundEvents.PLAYER_HURT;
    }
    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() {
        return net.minecraft.sounds.SoundEvents.PLAYER_DEATH;
    }
}
