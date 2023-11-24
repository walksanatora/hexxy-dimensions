package net.walksanator.hexdim

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.util.Identifier
import net.walksanator.hexdim.blocks.BlockRegistry
import net.walksanator.hexdim.render.HexxyDimensionShaders
import net.walksanator.hexdim.render.block.SkyboxRenderBlockRender

object HexxyDimensionsClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		CoreShaderRegistrationCallback.EVENT.register { ctx ->
			ctx.register(
				Identifier.of("hexdim", HexxyDimensionShaders.DIM_SHADER_ID),
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL
			) { shaderProgram -> HexxyDimensionShaders.dimShader = shaderProgram }
		}

		BlockEntityRendererFactories.register(BlockRegistry.SKYBOX_ENTITY) { SkyboxRenderBlockRender(it) }
	}
}