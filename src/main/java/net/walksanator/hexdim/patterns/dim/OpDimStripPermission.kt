package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.walksanator.hexdim.iotas.RoomIota

class OpDimStripPermission : ConstMediaAction {
    override val argc: Int = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
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