package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomIota

class OpQueuePos : ConstMediaAction {
    override val argc = 1
    override val isGreat: Boolean = true
    override fun execute(args: List<Iota>, env: CastingContext): List<Iota> {
        val iota = args[0]
        if (iota.type != RoomIota.TYPE) {throw MishapInvalidIota(iota,0, Text.literal("Room Iota")) } //TODO: make a translation string for room iota
        val storage = HexxyDimensions.STORAGE.get()
        val payload = (iota as RoomIota).pay
        storage.all[payload.first].keyCheckNoCarveCheck(payload.second)
        val indexes = storage.getCarveQueueIdxs()
        for (queue in 0..3) {
            for ((i,v) in indexes[queue].withIndex()) {
                if (v == payload.first) {
                    return listOf(DoubleIota(queue.toDouble()), DoubleIota(i.toDouble()))
                }
            }
        }
        return listOf(NullIota(),NullIota())
    }

}