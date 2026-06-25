package com.fightclass3.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Invisible punch tool. Does not render in hand. Damage = wooden sword (4). */
public class PunchItem extends Item {
    public PunchItem() {
        super(new Properties().stacksTo(1).fireResistant());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, net.minecraft.world.level.block.state.BlockState state) {
        return 1.0f;
    }

    @Override
    public boolean isFoil(ItemStack stack) { return false; }
}
