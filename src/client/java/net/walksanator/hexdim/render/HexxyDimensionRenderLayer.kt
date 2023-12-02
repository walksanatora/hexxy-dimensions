package net.walksanator.hexdim.render

import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats


class HexxyDimensionRenderLayer {
    companion object {
        val NATURE: RenderLayer = RenderLayer.of(
            "hexxy-dim",
            VertexFormats.POSITION,
            DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder().shader(
                RenderPhase.Shader { HexxyDimensionShaders.dimShader }
            ).build(true)
        )
    }
}