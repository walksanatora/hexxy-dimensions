package net.walksanator.hexdim.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.util.math.Vec3d
import net.walksanator.hexdim.HexxyDimensions
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

    override fun getRoomIndex(): Pair<Int, Int> = pay

    override fun getRoomValue(): Room {
        val storage = HexxyDimensions.STORAGE.get()
        val room = storage.all[pay.first]
        room.keyCheck(pay.second)
        return room
    }

    override fun getTeleportPosition(): Vec3d {
        val room = getRoomValue()
        val pos = Vec3d(
            room.getX().toDouble(),
            room.height.toDouble(),
            room.getY().toDouble()
        )
        return if (offsetPos.isPresent) {
            val offset = offsetPos.get()
            pos.add(Vec3d(offset.toVector3f()))
        } else {
            pos
        }

    }
}