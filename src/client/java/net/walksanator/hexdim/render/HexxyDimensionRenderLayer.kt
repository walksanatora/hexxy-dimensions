package net.walksanator.hexdim.render

import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase.*
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer


class HexxyDimensionRenderLayer {
    companion object {
        val NATURE: RenderLayer = RenderLayer.of(
            "hexxy-dim",
            VertexFormats.POSITION,
            DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder().program(
                ShaderProgram { HexxyDimensionShaders.dimShader }
            ).build(false)
        );

    }
}