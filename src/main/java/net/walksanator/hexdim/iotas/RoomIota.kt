package net.walksanator.hexdim.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement

class RoomIota(private val pay: Pair<Int,Int>) : Iota(TYPE,pay) {
    override fun isTruthy(): Boolean = true

    override fun toleratesOther(p0: Iota?): Boolean {
        return if (p0 is RoomIota) {
            p0.payload == payload
        } else {
            false
        }
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