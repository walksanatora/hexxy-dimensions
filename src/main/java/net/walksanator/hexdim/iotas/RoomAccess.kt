package net.walksanator.hexdim.iotas

import net.minecraft.util.math.Vec3d
import net.walksanator.hexdim.util.Room

interface RoomAccess {
    fun getRoom(): Pair<Int,Int>
    fun modifyTeleportPosition(room: Room, pos: Vec3d): Vec3d {
        return pos
    }
}