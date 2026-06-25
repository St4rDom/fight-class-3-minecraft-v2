package com.fightclass3.commands;

import com.fightclass3.capability.PlayerStatsCapability;
import com.fightclass3.event.PlayerEventHandler;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.fightclass3.FightClass3Mod;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FightClass3Mod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FctModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("fctmod")
                .requires(src -> src.hasPermission(2))

                .then(Commands.literal("SetStrength")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                        .executes(ctx -> setStrength(ctx))))

                .then(Commands.literal("SetVitality")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0, 500))
                        .executes(ctx -> setVitality(ctx))))

                .then(Commands.literal("SetAgility")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                        .executes(ctx -> setAgility(ctx))))

                .then(Commands.literal("GetAllAch")
                    .executes(ctx -> getAllAch(ctx)))

                .then(Commands.literal("GetSpeciality")
                    .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            builder.suggest("insanity"); builder.suggest("paintolerance"); return builder.buildFuture();
                        })
                        .executes(ctx -> getSpeciality(ctx))))
        );
    }

    private static int setStrength(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        int val = IntegerArgumentType.getInteger(ctx, "value");
        player.getCapability(PlayerStatsCapability.INSTANCE).ifPresent(s -> {
            s.setStrength(val); PlayerEventHandler.syncStatsToPlayer(player);
        });
        ctx.getSource().sendSuccess(() -> Component.literal("[FCT] Strength set to " + val), false);
        return val;
    }

    private static int setVitality(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        int val = IntegerArgumentType.getInteger(ctx, "value");
        player.getCapability(PlayerStatsCapability.INSTANCE).ifPresent(s -> {
            s.setVitality(val); PlayerEventHandler.syncStatsToPlayer(player);
        });
        ctx.getSource().sendSuccess(() -> Component.literal("[FCT] Vitality set to " + val), false);
        return val;
    }

    private static int setAgility(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        int val = IntegerArgumentType.getInteger(ctx, "value");
        player.getCapability(PlayerStatsCapability.INSTANCE).ifPresent(s -> {
            s.setAgility(val); PlayerEventHandler.syncStatsToPlayer(player);
        });
        ctx.getSource().sendSuccess(() -> Component.literal("[FCT] Agility set to " + val), false);
        return val;
    }

    private static int getAllAch(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        String[] advs = {"imfree", "castle_of_fortitude"};
        for (String id : advs) {
            var adv = player.server.getAdvancements().getAdvancement(
                    new net.minecraft.resources.ResourceLocation(FightClass3Mod.MOD_ID, id));
            if (adv != null) {
                var prog = player.getAdvancements().getOrStartProgress(adv);
                if (!prog.isDone())
                    adv.getCriteria().keySet().forEach(c -> player.getAdvancements().award(adv, c));
            }
        }
        player.getCapability(PlayerStatsCapability.INSTANCE).ifPresent(s -> {
            s.setTitle("Psychopath");
            s.unlockSpeciality("Insanity");
            s.unlockSpeciality("PainTolerance");
            PlayerEventHandler.syncStatsToPlayer(player);
        });
        ctx.getSource().sendSuccess(() -> Component.literal("[FCT] All achievements and specialities granted."), false);
        return 1;
    }

    private static int getSpeciality(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        String type = StringArgumentType.getString(ctx, "type").toLowerCase();
        String spec = type.equals("insanity") ? "Insanity" : type.equals("paintolerance") ? "PainTolerance" : null;
        if (spec == null) {
            ctx.getSource().sendFailure(Component.literal("[FCT] Unknown speciality. Use: insanity | paintolerance"));
            return 0;
        }
        final String fSpec = spec;
        player.getCapability(PlayerStatsCapability.INSTANCE).ifPresent(s -> {
            s.unlockSpeciality(fSpec);
            s.setActiveSpeciality(fSpec);
            PlayerEventHandler.syncStatsToPlayer(player);
        });
        ctx.getSource().sendSuccess(() -> Component.literal("[FCT] Speciality " + fSpec + " unlocked and activated."), false);
        return 1;
    }
}
