package com.fightclass3.items;

import com.fightclass3.capability.PlayerStatsCapability;
import com.fightclass3.event.PlayerEventHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class WillpowerItem extends Item {
    public WillpowerItem() { super(new Properties().stacksTo(16).rarity(net.minecraft.world.item.Rarity.RARE)); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            sp.getCapability(PlayerStatsCapability.INSTANCE).ifPresent(stats -> {
                stats.unlockSpeciality("PainTolerance");
                if (stats.getActiveSpeciality().equals("None"))
                    stats.setActiveSpeciality("PainTolerance");
                PlayerEventHandler.syncStatsToPlayer(sp);
                sp.sendSystemMessage(Component.literal("\u00a75[FCT] \u00a7fYour willpower has hardened. Pain Tolerance speciality unlocked."));
            });
            player.getItemInHand(hand).shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag flag) {
        tips.add(Component.literal("\u00a77Grants the Pain Tolerance speciality when used."));
        tips.add(Component.literal("\u00a78Obtained by defeating Jiu Ji-Tae with 4 hearts or less."));
    }

    @Override public boolean isFoil(ItemStack stack) { return true; } // glowing enchant effect
}
