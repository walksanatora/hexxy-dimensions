package net.walksanator.hexdim.patterns

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexActions
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.patterns.dim.*
import java.util.function.BiConsumer


object DimPatternRegistry {

    private val ACTIONS: MutableMap<Identifier, ActionRegistryEntry> =
        LinkedHashMap()

    val DIM_CREATE = pattern("wawdwawawdwawawdwewdwqwdwqwdwqwdwqwdwqwdw",HexDir.SOUTH_WEST,"dim/create",OpCreateDimension()) //
    val DIM_KIDNAP = pattern("wawewawewawewawewawewawwwqwqwqwqwqwaeqqqqqaww",HexDir.SOUTH_WEST,"dim/kidnap",OpKidnap())
    val DIM_BANISH = pattern("wwdeeeeeqdwewewewewewwwdwqwdwqwdwqwdwqwdwqwdw", HexDir.EAST, "dim/kick",OpBanish()) //
    val DIM_KEY = pattern("awqwawqdqawwwaq",HexDir.SOUTH_EAST,"dim/pos/set",OpDimSetPos()) //
    val DIM_POSKEY = pattern("dewedaewdwewd",HexDir.EAST,"dim/perm/remove",OpDimStripPermission()) //
    val DIM_TORELATIVE = pattern("adeeda",HexDir.EAST,"dim/rel/to",OpDimRelative(true)) //
    val DIM_FROMRELATIVE = pattern("daqqad",HexDir.NORTH_EAST,"dim/rel/from",OpDimRelative(false)) //
    val DIM_ACTIVATE = pattern("deaqqeweeeeewdqdqdqdqdq", HexDir.SOUTH_EAST,"dim/cast/activate", OpDimExecute(true)) //
    val DIM_DEACTIVATE = pattern("aqdeeqeaeaeaeaeae",HexDir.SOUTH_WEST, "dim/cast/deactivate", OpDimExecute(false)) //
    val DIM_CARVED = pattern("qqqqqwaeaeaeaeaeadwaqaeaq", HexDir.NORTH_WEST, "dim/carved",OpDimCarved()) //
    val DIM_ESTIMATE_TIME = pattern("qqqqqwaeaeaeaeaeadqwdwqwdwdwqw", HexDir.NORTH_EAST, "dim/time",OpEstimateTime())

    fun registerPatterns() {
        val r = BiConsumer { type: ActionRegistryEntry, id: Identifier -> Registry.register(HexActions.REGISTRY, id, type) }
        for ((key, value) in ACTIONS) {
            r.accept(value, key)
        }
    }

    private fun pattern(pat: String, dir: HexDir, name: String, oa: Action): ActionRegistryEntry {
        val are = ActionRegistryEntry(HexPattern.fromAngles(pat,dir),oa)
        val old = ACTIONS.put(Identifier(HexxyDimensions.MOD_ID,name), are)
        if (old != null) {
            throw IllegalArgumentException("Typo? Duplicate id $name")
        }
        return are
    }
}