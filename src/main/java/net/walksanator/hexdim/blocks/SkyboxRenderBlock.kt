package net.walksanator.hexdim.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class SkyboxRenderBlock(settings: Settings) : BlockWithEntity(settings) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = SkyboxRenderBlockEntity(pos,state)
}

class SkyboxRenderBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockRegistry.SKYBOX_ENTITY,pos,state) {
    init {
        println("spawned the entity!")
    }
    fun shouldDrawSide(direction: Direction?): Boolean {
        return Block.shouldDrawSide(cachedState, world, getPos(), direction, getPos().offset(direction))
    }
}