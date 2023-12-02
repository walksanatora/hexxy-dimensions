package net.walksanator.hexdim.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.util.math.Vec3d
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.util.Room

open class RoomIota(val pay: Pair<Int,Int>) : Iota(TYPE,pay), RoomAccess {
    override fun isTruthy(): Boolean = true

    override fun getRoomIndex(): Pair<Int, Int> = pay
    override fun getRoomValue(): Room {
        val storage = HexxyDimensions.STORAGE.get()
        val room = storage.all[pay.first]
        room.keyCheck(pay.second)
        return room
    }
    override fun getTeleportPosition(): Vec3d {
        val room = getRoomValue()
        return Vec3d(
            (room.getX().toDouble() + (room.getW().toDouble()/2)),
            room.height.toDouble()/2,
            (room.getY().toDouble() + (room.getH().toDouble()/2))
        )
    }

    fun downgradeToEntry(): EntryIota = EntryIota(pay)

    override fun toleratesOther(p0: Iota?): Boolean {
        if (p0 != null) {
            return if (p0.type == TYPE) {
                (p0 as RoomIota).payload == payload
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
        return nbt
    }

    companion object {
        val TYPE = RoomIotaType()
    }
}