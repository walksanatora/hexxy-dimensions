package net.walksanator.hexdim.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement

open class RoomIota(val pay: Pair<Int,Int>) : Iota(TYPE,pay), RoomAccess {
    override fun isTruthy(): Boolean = true

    override fun getRoom(): Pair<Int, Int> = pay

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