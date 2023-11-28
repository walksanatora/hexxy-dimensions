package net.walksanator.hexdim.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.util.math.Vec3d
import net.walksanator.hexdim.util.Room
import java.util.*

class EntryIota(val pay: Pair<Int,Int>) : Iota(TYPE,pay), RoomAccess {
    private var offsetPos = Optional.empty<Vec3d>()

    constructor(pay: Pair<Int,Int>, offset: Vec3d) : this(pay) {
        offsetPos = Optional.of(offset)
    }

    companion object {
        val TYPE = EntryIotaType()
    }

    override fun modifyTeleportPosition(room: Room, pos: Vec3d): Vec3d {
        if (offsetPos.isEmpty) {return pos}
        val offset = offsetPos.get()
        if (room.getW() >= offset.x+0.5) {
            if (room.height >= offset.y) {
                if (room.getH() >= offset.z+0.5) {
                    return Vec3d(
                        room.getX() + offset.x+0.5,
                        offset.y,
                        room.getY() + offset.z+0.5,
                    )
                }
            }
        }
        return pos
    }

    override fun isTruthy(): Boolean  = true

    override fun toleratesOther(p0: Iota?): Boolean {
        if (p0 != null) {
            return if (p0.type == TYPE) {
                (p0 as EntryIota).payload == payload
            } else {
                false
            }
        }
        return false
    }

    override fun serialize(): NbtElement {
        val nbt = NbtCompound()
        nbt.putInt("idx",pay.first)
        nbt.putInt("key",pay.second)
        nbt.putBoolean("doOffset",offsetPos.isPresent)
        if (offsetPos.isPresent) {
            val pos = offsetPos.get()
            nbt.putDouble("ox",pos.x)
            nbt.putDouble("oy",pos.y)
            nbt.putDouble("oz",pos.z)
        }
        return nbt
    }

    override fun getRoom(): Pair<Int, Int> = pay
}