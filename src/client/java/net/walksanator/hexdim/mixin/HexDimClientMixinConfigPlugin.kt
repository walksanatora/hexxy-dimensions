package net.walksanator.hexdim.mixin

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class HexDimClientMixinConfigPlugin : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String) {
    }

    override fun getRefMapperConfig(): String? {
        return null
    }

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        if(mixinClassName.contains("vanilla") && (classExists("me.jellysquid.mods.sodium.client.SodiumClientMod") || classExists("net.irisshaders.iris.api.v0.IrisApi")))
            return false;
        return true;
    }


    override fun acceptTargets(myTargets: Set<String>, otherTargets: Set<String>) {
    }

    override fun getMixins(): List<String>? {
        return null
    }

    override fun preApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
    }

    override fun postApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
    }

    companion object {
        private fun classExists(className: String): Boolean {
            try {
                Class.forName(className, false, HexDimClientMixinConfigPlugin::class.java.classLoader)
                return true
            } catch (ex: ClassNotFoundException) {
                return false
            }
        }
    }
}
