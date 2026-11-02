package com.vyn.disguise;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

/**
 * Names and textures come from the resource files inside this mod
 * (assets/minecraft/...). This class only adds the extra break sounds,
 * and only on your own client.
 */
public class DisguiseClient implements ClientModInitializer {

    private static BlockPos trackedPos = null;
    private static Block trackedBlock = null;
    private static int age = 0;

    @Override
    public void onInitializeClient() {
        System.out.println("[ItemDisguise] loaded");

        // remember which disguised block you started breaking
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (world.isClient()) {
                Block b = world.getBlockState(pos).getBlock();
                if (isDisguised(b)) {
                    trackedPos = pos.toImmutable();
                    trackedBlock = b;
                    age = 0;
                }
            }
            return ActionResult.PASS;
        });

        // when that block turns into air, it was broken -> play the extra sound
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (trackedPos == null || client.world == null || client.player == null) return;
            age++;
            BlockState now = client.world.getBlockState(trackedPos);
            boolean far = client.player.getBlockPos().getSquaredDistance(trackedPos) > 64;
            if (!far && now.isAir()) {
                playBreak(trackedBlock);
                clear();
            } else if (far || now.getBlock() != trackedBlock || age > 2400) {
                clear();
            }
        });
    }

    private static void clear() {
        trackedPos = null;
        trackedBlock = null;
        age = 0;
    }

    private static boolean isDisguised(Block b) {
        return b == Blocks.BLUE_GLAZED_TERRACOTTA
                || b == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE
                || b == Blocks.SEA_LANTERN
                || b == Blocks.WITHER_ROSE
                || b == Blocks.STICKY_PISTON;
    }

    private static void playBreak(Block b) {
        if (b == Blocks.BLUE_GLAZED_TERRACOTTA) play(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA);          // "Elytra"
        else if (b == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) play(SoundEvents.BLOCK_METAL_BREAK);     // "Spawner"
        else if (b == Blocks.SEA_LANTERN) play(SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK);             // "Netherite Block"
        else if (b == Blocks.WITHER_ROSE) play(SoundEvents.ITEM_MACE_SMASH_GROUND);                  // "Mace"
        else if (b == Blocks.STICKY_PISTON) play(SoundEvents.BLOCK_GILDED_BLACKSTONE_BREAK);         // "Gilded Blackstone"
    }

    // two overloads so it compiles whether a sound constant is a SoundEvent or a RegistryEntry
    private static void play(SoundEvent s) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(s, 1.0f, 1.0f));
    }

    private static void play(RegistryEntry<SoundEvent> e) {
        play(e.value());
    }
}
