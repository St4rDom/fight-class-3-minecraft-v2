package com.fightclass3.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class FightKeys {
    public static final KeyMapping OPEN_STATS = new KeyMapping(
            "key.fightclass3.stats",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_SEMICOLON,
            "key.categories.fightclass3"
    );
}
