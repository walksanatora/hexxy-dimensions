package net.walksanator.hexdim.casting

import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.walksanator.hexdim.util.Room

class HexDimComponents {
    class VecInRange(val oldWorld: ServerWorld, val room: Room) : CastingEnvironmentComponent.IsVecInRange {
        class Key : CastingEnvironmentComponent.Key<VecInRange>
        companion object {val KEY = Key()}
        override fun getKey(): CastingEnvironmentComponent.Key<*> = KEY

        override fun onIsVecInRange(p0: Vec3d, p1: Boolean): Boolean = withinRoom(room,p0)
    }
    class HasPermissionsAt(val room: Room) : CastingEnvironmentComponent.HasEditPermissionsAt {
        class Key : CastingEnvironmentComponent.Key<HasPermissionsAt>
        companion object {val KEY = Key()}
        override fun getKey(): CastingEnvironmentComponent.Key<*> = KEY

        override fun onHasEditPermissionsAt(p0: BlockPos, p1: Boolean): Boolean = withinRoom(room,Vec3d(
            p0.x.toDouble(),
            p0.y.toDouble(),
            p0.z.toDouble()
        ))
    }
    companion object {
        fun withinRoom(room: Room, pos: Vec3d): Boolean {
            val withinX = room.getX() <= pos.x && pos.x <= room.getX() + room.getW()
            val withinY = pos.x <= room.height
            val withinZ = room.getY() <= pos.z && pos.z <= room.getY() + room.getH()
            return withinX && withinY && withinZ
        }
    }
}