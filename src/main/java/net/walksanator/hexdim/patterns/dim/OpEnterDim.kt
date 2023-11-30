package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomAccess

class OpEnterDim : ConstMediaAction {
    override val argc = 1
    override val mediaCost: Long = MediaConstants.SHARD_UNIT
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args[0]
        if (target !is RoomAccess ) { throw MishapInvalidIota(target,0, Text.literal("Expected Room-like Iota")) } //TODO: make translation string
        val storage = HexxyDimensions.STORAGE.get()
        val payload = (target as RoomAccess).getRoom()
        val index = payload.first
        val key = payload.second

        val room = storage.all[index]
        room.keyCheck(key)

        val caster = env.caster ?: return emptyList()
        //TODO: make mishap for environment has no caster
        val pos = target.modifyTeleportPosition(room,Vec3d(
            (room.getX().toDouble() + (room.getW().toDouble()/2)),
            room.height.toDouble()/2,
            (room.getY().toDouble() + (room.getH().toDouble()/2))
        ))
        FabricDimensions.teleport(
            caster, storage.world!!,
            TeleportTarget(
                pos,
                Vec3d.ZERO,
                0F,0F
            )
        )

        return listOf()
    }
}