package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomIota

class OpDimCarved : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val iota = args[0]
        if (iota.type != RoomIota.TYPE) {
            throw MishapInvalidIota(iota,0, Text.translatable("hexdim.iota.room"))
        }
        val storage = HexxyDimensions.STORAGE.get()
        val payload = (iota as RoomIota).pay
        val room = storage.all[payload.first]
        room.keyCheckNoCarveCheck(payload.second)
        return listOf(BooleanIota(room.isDone))
    }
}