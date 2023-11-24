package net.walksanator.hexdim.render

import net.minecraft.client.gl.ShaderProgram

class HexxyDimensionShaders {
    companion object {
        const val DIM_SHADER_ID = "rendertype_hexdim_skybox"
        var dimShader: ShaderProgram? = null
    }
}