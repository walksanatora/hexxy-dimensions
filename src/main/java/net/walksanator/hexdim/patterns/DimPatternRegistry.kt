package net.walksanator.hexdim.patterns

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexActions
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.patterns.dim.OpCreateDimension
import java.util.function.BiConsumer


object DimPatternRegistry {

    private val ACTIONS: MutableMap<Identifier, ActionRegistryEntry> =
        LinkedHashMap()

    //SOUTH_WEST wawdwawawdwawawdwewdwqwdwqwdwqwdwqwdwqwdw
    val DIM_CREATE = pattern("wawdwawawdwawawdwewdwqwdwqwdwqwdwqwdwqwdw",HexDir.SOUTH_WEST,"dim/create",OpCreateDimension())

    fun registerPatterns() {
        val r = BiConsumer { type: ActionRegistryEntry, id: Identifier -> Registry.register(HexActions.REGISTRY, id, type) }
        for ((key, value) in ACTIONS) {
            r.accept(value, key)
        }
    }

    fun pattern(pat: String, dir: HexDir, name: String, oa: Action): ActionRegistryEntry {
        val are = ActionRegistryEntry(HexPattern.fromAngles(pat,dir),oa)
        val old = ACTIONS.put(Identifier(HexxyDimensions.MOD_ID,name), are)
        if (old != null) {
            throw IllegalArgumentException("Typo? Duplicate id $name")
        }
        return are
    }
}