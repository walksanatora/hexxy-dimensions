package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.BooleanIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomIota

class OpDimCarved : ConstMediaAction {
    override val argc = 1
    override val isGreat: Boolean = true
    override fun execute(args: List<Iota>, env: CastingContext): List<Iota> {
        val iota = args[0]
        if (iota.type != RoomIota.TYPE) {throw MishapInvalidIota(iota,0, Text.literal("Room Iota"))
        } //TODO: make a translation string for room iota
        val storage = HexxyDimensions.STORAGE.get()
        val payload = (iota as RoomIota).pay
        val room = storage.all[payload.first]
        room.keyCheckNoCarveCheck(payload.second)
        return listOf(BooleanIota(room.isDone))
    }
}