package net.walksanator.hexdim.blocks

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.piston.PistonBehavior


class BlockRegistry {
    companion object {
        val SKYBOX: Block = Block(FabricBlockSettings.create().collidable(true).dropsNothing().luminance(15).mapColor(
            MapColor.TERRACOTTA_PURPLE).strength(-1.0F, 3600000.0F).pistonBehavior(PistonBehavior.BLOCK))
        val SKYBOX_ENTITY: BlockEntityType<SkyboxRenderBlockEntity> =
            BlockEntityType.Builder.create({pos,state -> SkyboxRenderBlockEntity(pos,state) }, SKYBOX).build(null)

    }
}