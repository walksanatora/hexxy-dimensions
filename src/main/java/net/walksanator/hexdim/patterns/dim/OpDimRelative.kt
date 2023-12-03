package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomIota

class OpDimRelative(private val convertTo: Boolean) : ConstMediaAction {
    override val argc = 2
    override val isGreat: Boolean = true
    override fun execute(args: List<Iota>, env: CastingContext): List<Iota> {
        val input = args[1]
        if (input.type != Vec3Iota.TYPE) {throw MishapInvalidIota(input,1, Text.translatable("hexcasting.iota.hexcasting:vec3")) }
        val room = args[0]
        if (room.type != RoomIota.TYPE) {throw MishapInvalidIota(room,0, Text.literal("expected Room Iota"))} //TODO: make translation string
        val payload = (room as RoomIota).pay
        val roomInstance = HexxyDimensions.STORAGE.get().all[payload.first]
        roomInstance.keyCheckNoCarveCheck(payload.second) // this mishaps if room was deleted
        val v3 = (input as Vec3Iota).vec3
        return listOf(Vec3Iota(if (convertTo) {
            Vec3d(
                v3.x - roomInstance.getX(),
                v3.y,
                v3.z - roomInstance.getY()
            )
        } else {
            Vec3d(
                roomInstance.getX() + v3.x,
                v3.y,
                roomInstance.getY() + v3.z
            )
        }))
    }
}