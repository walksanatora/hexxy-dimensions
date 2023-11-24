package net.walksanator.hexdim.render

import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase.ENABLE_LIGHTMAP
import net.minecraft.client.render.RenderPhase.MIPMAP_BLOCK_ATLAS_TEXTURE
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.RenderPhase.ShaderProgram


class HexxyDimensionRenderLayer {
    companion object {
        @JvmStatic
        val NATURE: RenderLayer = RenderLayer.of(
            "hexxy-dimension\$skybox",
            VertexFormats.POSITION,
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                .program(ShaderProgram { HexxyDimensionShaders.dimShader })
                .build(true)
        )
    }
}