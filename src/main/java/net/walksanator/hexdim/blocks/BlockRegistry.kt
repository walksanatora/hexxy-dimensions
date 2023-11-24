package net.walksanator.hexdim.blocks

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntityType


class BlockRegistry {
    companion object {
        val SKYBOX: Block = SkyboxRenderBlock(FabricBlockSettings.copy(Blocks.END_GATEWAY))
        val SKYBOX_ENTITY: BlockEntityType<SkyboxRenderBlockEntity> =
            BlockEntityType.Builder.create({pos,state -> SkyboxRenderBlockEntity(pos,state) }, SKYBOX).build(null)

    }
}