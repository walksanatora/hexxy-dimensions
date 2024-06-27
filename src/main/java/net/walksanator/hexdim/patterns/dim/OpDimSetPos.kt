package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.walksanator.hexdim.iotas.RoomIota

class OpDimSetPos() : ConstMediaAction {
    override val argc: Int = 2
    override fun execute(args: List<Iota>, env: CastingContext): List<Iota> {
        val room = args[0]
        if (room !is RoomIota) {throw MishapInvalidIota(room,0, Text.translatable("hexdim.iota.room"))}
        val pos = args[1]
        if (!(pos is Vec3Iota || pos is NullIota)) {throw MishapInvalidIota(room,1,Text.translatable("hexcasting.iota.hexcasting:vec3"))}
        if (!room.permissions[1]) {throw MishapInvalidIota(room,0,Text.translatable("hexdim.iota.permissions.write"))}
        return listOf(room.copy(spawnpos = if (pos is NullIota) {null} else {(pos as Vec3Iota).vec3 }))
    }

}