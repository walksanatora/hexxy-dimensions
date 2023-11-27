package net.walksanator.hexdim

import com.google.common.collect.ImmutableList
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.util.Identifier
import net.walksanator.hexdim.blocks.BlockRegistry
import net.walksanator.hexdim.render.HexxyDimensionShaders
import net.walksanator.hexdim.render.block.SkyboxRenderBlockRender
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.walksanator.hexdim.render.HexxyDimensionRenderLayer

object HexxyDimensionsClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		CoreShaderRegistrationCallback.EVENT.register { ctx ->
			ctx.register(
				Identifier.of("hexdim", HexxyDimensionShaders.DIM_SHADER_ID),
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL
			) { shaderProgram -> HexxyDimensionShaders.dimShader = shaderProgram }
		}

		val layers = ArrayList(RenderLayer.BLOCK_LAYERS)
		layers.add(HexxyDimensionRenderLayer.NATURE)
		RenderLayer.BLOCK_LAYERS = ImmutableList.copyOf(layers)

		BlockEntityRendererFactories.register(BlockRegistry.SKYBOX_ENTITY) { SkyboxRenderBlockRender(it) }

		BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.SKYBOX,HexxyDimensionRenderLayer.NATURE)

		WorldRenderEvents.BEFORE_ENTITIES.register { ctx ->
			val camPos = ctx.camera().pos
			ctx.worldRenderer();
			ctx.worldRenderer().renderLayer(HexxyDimensionRenderLayer.NATURE,ctx.matrixStack(),camPos.x,camPos.y,camPos.z,ctx.projectionMatrix())
		}

	}
}