package net.walksanator.hexdim.render.block

import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.BlockRenderManager
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.walksanator.hexdim.blocks.SkyboxRenderBlockEntity
import net.walksanator.hexdim.render.HexxyDimensionRenderLayer
import org.joml.Matrix4f


class SkyboxRenderBlockRender(ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<SkyboxRenderBlockEntity> {

    override fun render(
        endPortalBlockEntity: SkyboxRenderBlockEntity,
        f: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        j: Int
    ) {
        val blockRender: BlockRenderManager = MinecraftClient.getInstance().blockRenderManager

        matrixStack.push()
        blockRender.renderBlock(
            Blocks.CHISELED_STONE_BRICKS.defaultState,
            endPortalBlockEntity.getPos(),
            MinecraftClient.getInstance().world,
            matrixStack,
            vertexConsumerProvider.getBuffer(getLayer()),
            true,
            Random.create()
        )
        matrixStack.pop()
        return

        val matrix4f = matrixStack.peek().positionMatrix
        renderSides(endPortalBlockEntity, matrix4f, vertexConsumerProvider.getBuffer(this.getLayer()))
    }

    private fun renderSides(entity: SkyboxRenderBlockEntity, matrix: Matrix4f, vertexConsumer: VertexConsumer) {
        val f = getBottomYOffset()
        val g = getTopYOffset()
        renderSide(entity, matrix, vertexConsumer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH)
        renderSide(entity, matrix, vertexConsumer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH)
        renderSide(entity, matrix, vertexConsumer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST)
        renderSide(entity, matrix, vertexConsumer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.WEST)
        renderSide(entity, matrix, vertexConsumer, 0.0f, 1.0f, f, f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN)
        renderSide(entity, matrix, vertexConsumer, 0.0f, 1.0f, g, g, 1.0f, 1.0f, 0.0f, 0.0f, Direction.UP)
    }

    private fun renderSide(
        entity: SkyboxRenderBlockEntity,
        model: Matrix4f,
        vertices: VertexConsumer,
        x1: Float,
        x2: Float,
        y1: Float,
        y2: Float,
        z1: Float,
        z2: Float,
        z3: Float,
        z4: Float,
        side: Direction
    ) {
        if (entity.shouldDrawSide(side)) {
            vertices.vertex(model, x1, y1, z1).next()
            vertices.vertex(model, x2, y1, z2).next()
            vertices.vertex(model, x2, y2, z3).next()
            vertices.vertex(model, x1, y2, z4).next()
        }
    }

    private fun getTopYOffset(): Float {
        return 1f
    }

    private fun getBottomYOffset(): Float {
        return 0f
    }
    private fun getLayer(): RenderLayer = HexxyDimensionRenderLayer.NATURE
}