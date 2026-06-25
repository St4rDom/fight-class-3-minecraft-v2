package com.fightclass3.gui;

import com.fightclass3.client.ClientStatsCache;
import com.fightclass3.network.PacketHandler;
import com.fightclass3.network.SetStatPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

/**
 * Fight Class 3 Stat Menu.
 * Shows: player render | Title | Strength / Vitality / Agility | Speciality selector.
 */
public class StatMenuScreen extends Screen {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final int BG       = 0xF0050510;
    private static final int SURFACE  = 0xCC0D0D22;
    private static final int BORDER   = 0xFFCC1122;
    private static final int ACCENT   = 0xFF880011;
    private static final int TEXT_H   = 0xFFFFCCCC;
    private static final int TEXT_S   = 0xFFAA8888;
    private static final int TEXT_DIM = 0xFF553333;
    private static final int BAR_BG   = 0xFF1A0010;
    private static final int BAR_STR  = 0xFFCC2233;
    private static final int BAR_VIT  = 0xFF33BB44;
    private static final int BAR_AGI  = 0xFF2255CC;

    private static final int W = 320, H = 240;

    public StatMenuScreen() { super(Component.literal("FCT Stats")); }

    @Override
    protected void init() {
        super.init();
        int L = (width-W)/2, T = (height-H)/2;
        int btnY = T + 190;

        // Speciality switch buttons (only show if unlocked)
        if (ClientStatsCache.unlockedSpecialities.size() >= 2 ||
            (ClientStatsCache.unlockedSpecialities.size() == 1 && !ClientStatsCache.activeSpeciality.equals("None"))) {
            
            for (int i = 0; i < ClientStatsCache.unlockedSpecialities.size() && i < 2; i++) {
                String spec = ClientStatsCache.unlockedSpecialities.get(i);
                boolean active = spec.equals(ClientStatsCache.activeSpeciality);
                String lbl = (active ? "[" : "") + specLabel(spec) + (active ? "]" : "");
                final String fSpec = spec;
                addRenderableWidget(Button.builder(Component.literal(lbl),
                        btn -> PacketHandler.sendToServer(new SetStatPacket(SetStatPacket.Stat.ACTIVE_SPEC, fSpec)))
                        .bounds(L + 10 + i * 155, btnY, 150, 18).build());
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        int L = (width-W)/2, T = (height-H)/2;
        Minecraft mc = Minecraft.getInstance();

        // ── Panel ─────────────────────────────────────────────────────────────
        g.fill(L, T, L+W, T+H, BG);
        g.fill(L, T,     L+W, T+1,   BORDER);
        g.fill(L, T+H-1, L+W, T+H,   BORDER);
        g.fill(L, T,     L+1, T+H,   BORDER);
        g.fill(L+W-1,T,  L+W, T+H,   BORDER);
        g.fill(L+1, T+1, L+4, T+H-1, ACCENT);

        // ── Header ────────────────────────────────────────────────────────────
        g.fill(L+4, T+1, L+W-1, T+22, SURFACE);
        g.drawCenteredString(font, "FIGHT CLASS 3 — STATUS", L+W/2+1, T+8, TEXT_H);
        g.fill(L+4, T+22, L+W-1, T+23, BORDER);

        // ── Player render (left panel) ────────────────────────────────────────
        int pnlLeft = L+8, pnlTop = T+30, pnlW = 100, pnlH = 140;
        g.fill(pnlLeft, pnlTop, pnlLeft+pnlW, pnlTop+pnlH, SURFACE);
        g.fill(pnlLeft, pnlTop, pnlLeft+pnlW, pnlTop+1, BORDER);
        g.fill(pnlLeft, pnlTop+pnlH-1, pnlLeft+pnlW, pnlTop+pnlH, BORDER);
        if (mc.player != null) {
            int px = pnlLeft + pnlW/2;
            int py = pnlTop + pnlH - 15;
            InventoryScreen.renderEntityInInventory(g, px, py, 30, px - mx, py - 80 - my, mc.player);
        }

        // ── Right panel ───────────────────────────────────────────────────────
        int rL = L + 118;

        // Title
        g.fill(rL, T+30, L+W-8, T+48, SURFACE);
        g.drawString(font, "TITLE", rL+4, T+33, TEXT_DIM, false);
        g.drawString(font, ClientStatsCache.title, rL+50, T+33, 
                ClientStatsCache.title.equals("Psychopath") ? 0xFFFF2233 : TEXT_S, false);

        // Divider
        g.fill(rL, T+49, L+W-8, T+50, ACCENT);

        // Stats header
        g.drawString(font, "STATS", rL+4, T+54, TEXT_DIM, false);

        // Stat bars
        drawStatBar(g, "STR", ClientStatsCache.strength,  100, rL, T+66,  L+W-8-rL, BAR_STR);
        drawStatBar(g, "VIT", ClientStatsCache.vitality,  500, rL, T+90,  L+W-8-rL, BAR_VIT);
        drawStatBar(g, "AGI", ClientStatsCache.agility,   100, rL, T+114, L+W-8-rL, BAR_AGI);

        // Extra hearts from vitality
        int extraHearts = ClientStatsCache.vitality / 10;
        g.drawString(font, "+" + extraHearts + " hearts", rL + 4, T+106, 0xFF55FF55, false);

        // Divider
        g.fill(rL, T+138, L+W-8, T+139, ACCENT);
        g.drawString(font, "SPECIALITY", rL+4, T+143, TEXT_DIM, false);

        String specDisplay = ClientStatsCache.activeSpeciality.equals("None") ? "None"
                : specLabel(ClientStatsCache.activeSpeciality);
        g.drawString(font, specDisplay, rL+70, T+143,
                ClientStatsCache.activeSpeciality.equals("None") ? TEXT_DIM : 0xFFFF88AA, false);

        // Speciality description
        String desc = getSpecDesc(ClientStatsCache.activeSpeciality);
        if (!desc.isEmpty()) g.drawString(font, desc, rL+4, T+156, TEXT_DIM, false);

        // Section label
        g.fill(L+4, T+H-27, L+W-1, T+H-26, BORDER);
        g.drawString(font, "SWITCH SPECIALITY:", L+10, T+H-22, TEXT_DIM, false);

        g.drawCenteredString(font, "ESC to close", L+W/2+1, T+H-10, TEXT_DIM);
        super.render(g, mx, my, pt);
    }

    private void drawStatBar(GuiGraphics g, String label, int val, int max, int x, int y, int w, int color) {
        g.drawString(font, label, x+4, y, TEXT_S, false);
        int barX = x+28, barW = w-36, barH = 7;
        g.fill(barX, y, barX+barW, y+barH, BAR_BG);
        int fill = (int)((float)val/max * barW);
        if (fill > 0) g.fill(barX, y, barX+fill, y+barH, color);
        g.drawString(font, val+"/"+max, barX+barW+3, y, TEXT_DIM, false);
    }

    private static String specLabel(String spec) {
        return switch (spec) {
            case "Insanity"      -> "Insanity";
            case "PainTolerance" -> "Pain Tolerance";
            default              -> spec;
        };
    }

    private static String getSpecDesc(String spec) {
        return switch (spec) {
            case "Insanity"      -> "Night: NightVision | Day: Darkness+Outlines";
            case "PainTolerance" -> "Resistance V + Fire Res III + Regen III";
            default              -> "";
        };
    }

    @Override public boolean isPauseScreen() { return false; }
}
