package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomIota
import net.walksanator.hexdim.duck.ICastingContext

class OpDimExecute(private val activate: Boolean) : ConstMediaAction {
    override val argc: Int
        get() {
            return if (activate) {1} else {0}
        }
    override val isGreat: Boolean = true
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val envEnabled = (ctx as ICastingContext).`hexxy_dimensions$isModded`()
        val storage = HexxyDimensions.STORAGE.get()
        return if (activate) {
            if (envEnabled) {throw MishapDisallowedSpell()} // TODO: make mishap for allready in env
            val iota = args[0]
            if (iota.type != RoomIota.TYPE) {throw MishapInvalidIota(iota,0,Text.literal("Room iota"))} //TODO: use a translation string
            val room = (iota as RoomIota).getRoomValue()
            ctx.`hexxy_dimensions$setRoom`(room)
            ctx.`hexxy_dimensions$setWorld`(storage.world)
            listOf(iota)
        } else {
            if (!envEnabled) {throw MishapDisallowedSpell()} //TODO: make mishap for not in env
            ctx.`hexxy_dimensions$setRoom`(null)
            ctx.`hexxy_dimensions$setWorld`(null)
            listOf()
        }
    }
}