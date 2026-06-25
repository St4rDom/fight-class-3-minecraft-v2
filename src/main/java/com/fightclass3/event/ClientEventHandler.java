package com.fightclass3.event;

import com.fightclass3.FightClass3Mod;
import com.fightclass3.client.ClientStatsCache;
import com.fightclass3.client.FightKeys;
import com.fightclass3.gui.StatMenuScreen;
import com.fightclass3.items.PunchItem;
import com.fightclass3.network.PacketHandler;
import com.fightclass3.network.SetStatPacket;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FightClass3Mod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    /** Register the punch animation layer via Player Animator API. Called on mod init. */
    public static void registerAnimations() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                new ResourceLocation(FightClass3Mod.MOD_ID, "punch"),
                10,
                player -> new ModifierLayer<>()
        );
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        // Open stat menu with ; key
        if (FightKeys.OPEN_STATS.consumeClick()) {
            mc.setScreen(new StatMenuScreen());
        }
    }

    /** Trigger punch animation when player attacks with PunchItem. */
    @SubscribeEvent
    public static void onAttackInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!(mc.player.getMainHandItem().getItem() instanceof PunchItem)) return;

        // Trigger the punch animation via Player Animator
        PlayerAnimationAccess.getPlayerAnimLayer(mc.player).ifPresent(animatable -> {
            var layer = animatable.getAnimLayer(10);
            if (layer instanceof ModifierLayer<?> ml) {
                // Load animation from resource file
                var anim = getOrLoadPunchAnim(mc);
                if (anim != null) ((ModifierLayer<IAnimation>) ml).replaceAnimation(anim);
            }
        });
    }

    private static IAnimation cachedAnim = null;

    @SuppressWarnings("unchecked")
    private static IAnimation getOrLoadPunchAnim(Minecraft mc) {
        if (cachedAnim != null) return cachedAnim;
        try {
            var loc = new ResourceLocation(FightClass3Mod.MOD_ID, "animations/punch.json");
            var stream = mc.getResourceManager().open(loc);
            var animData = dev.kosmx.playerAnim.core.impl.AnimationProcessor.deserialize(
                    new java.io.InputStreamReader(stream));
            if (animData != null && !animData.isEmpty()) {
                cachedAnim = new dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer(animData.get(0));
            }
        } catch (Exception e) {
            // Animation file not found or invalid — silent fail
        }
        return cachedAnim;
    }
}
