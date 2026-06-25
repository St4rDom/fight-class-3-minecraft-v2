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

public class RecollectionItem extends Item {
    public RecollectionItem() { super(new Properties().stacksTo(16)); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            sp.getCapability(PlayerStatsCapability.INSTANCE).ifPresent(stats -> {
                stats.unlockSpeciality("Insanity");
                if (stats.getActiveSpeciality().equals("None"))
                    stats.setActiveSpeciality("Insanity");
                PlayerEventHandler.syncStatsToPlayer(sp);
                sp.sendSystemMessage(Component.literal("\u00a76[FCT] \u00a7fYou absorbed the Recollection. Insanity speciality unlocked."));
            });
            player.getItemInHand(hand).shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag flag) {
        tips.add(Component.literal("\u00a77Grants the Insanity speciality when used."));
    }
}
