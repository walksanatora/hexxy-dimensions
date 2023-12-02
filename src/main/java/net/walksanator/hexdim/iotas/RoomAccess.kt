package net.walksanator.hexdim.iotas

import net.minecraft.util.math.Vec3d
import net.walksanator.hexdim.util.Room

interface RoomAccess {
    fun getRoomIndex(): Pair<Int,Int>
    fun getRoomValue(): Room
    fun getTeleportPosition(): Vec3d
}