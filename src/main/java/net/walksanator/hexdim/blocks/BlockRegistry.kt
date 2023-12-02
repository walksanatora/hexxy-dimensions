package net.walksanator.hexdim.blocks

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.block.Material


class BlockRegistry {
    companion object {
        val SKYBOX: Block = SkyboxBlock(FabricBlockSettings.of(Material.WOOL).collidable(true).dropsNothing().luminance { 15 }
            .mapColor(
            MapColor.TERRACOTTA_PURPLE).strength(5F, 3600000.0F))
    }
}