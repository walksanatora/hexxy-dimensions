package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.walksanator.hexdim.iotas.RoomIota

class OpDimStripPermission : ConstMediaAction {
    override val argc: Int = 2
    override val isGreat: Boolean = true
    override fun execute(args: List<Iota>, env: CastingContext): List<Iota> {
        val room = args[0]
        if (room !is RoomIota) {throw MishapInvalidIota(room,0, Text.translatable("hexdim.iota.room"))}
        val pos = args[1]
        if (pos !is DoubleIota) {throw MishapInvalidIota(room,1, Text.translatable("hexcasting.iota.hexcasting:double"))}
        val pc = room.permissions.toMutableList()
        pc[pos.double.toInt()] = false
        val cpy = room.copy(permissions = pc)
        return listOf(cpy)
    }
}