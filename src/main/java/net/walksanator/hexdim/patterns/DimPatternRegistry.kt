package net.walksanator.hexdim.patterns

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.math.HexDir
import at.petrak.hexcasting.api.spell.math.HexPattern
import net.minecraft.util.Identifier
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.patterns.dim.*

object DimPatternRegistry {
    val DIM_CREATE = pattern("wawdwawawdwawawdwewdwqwdwqwdwqwdwqwdwqwdw",HexDir.SOUTH_WEST,"dim/create",OpCreateDimension(),true) //
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

    private fun pattern(pat: String, dir: HexDir, name: String, oa: Action) {
        PatternRegistry.mapPattern(HexPattern.fromAngles(pat,dir), Identifier(HexxyDimensions.MOD_ID,name), oa, false)
    }
    private fun pattern(pat: String, dir: HexDir, name: String, oa: Action, great: Boolean) {
        PatternRegistry.mapPattern(HexPattern.fromAngles(pat,dir), Identifier(HexxyDimensions.MOD_ID,name), oa, great)
    }

    fun loadme() {
        HexxyDimensions.logger.info("Registered patterns SIR. (insert indecipherable uwu speech)")
    }
}