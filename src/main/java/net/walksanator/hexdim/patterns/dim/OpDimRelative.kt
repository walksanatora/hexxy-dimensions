package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomIota

class OpDimRelative(private val convertTo: Boolean) : ConstMediaAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val input = args[1]
        if (input.type != Vec3Iota.TYPE) {throw MishapInvalidIota(input,1, Text.literal("expected Vec3 Iota"))} //TODO: use hexcasting's existing vec3 translation string
        val room = args[0]
        if (room.type != RoomIota.TYPE) {throw MishapInvalidIota(room,0,Text.literal("expected Room Iota"))} //TODO: make translation string
        val payload = (room as RoomIota).pay
        val roomInstance = HexxyDimensions.STORAGE.get().all[payload.first]
        roomInstance.keyCheck(payload.second) // this mishaps
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