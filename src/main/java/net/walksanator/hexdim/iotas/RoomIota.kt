package net.walksanator.hexdim.iotas


import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.util.math.Vec3d
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.util.Room

open class RoomIota(val pay: Pair<Int,Int>,val spawnpos: Vec3d?,var permissions: List<Boolean>) : Iota(TYPE,pay) {

    init {
        val pc = permissions.toMutableList()
        while (pc.size < PermissionStrings.field.size) {
            pc.add(PermissionStrings.defaults[pc.size+1])//copy defaults
        }
        permissions = pc
    }

    constructor(pay: Pair<Int, Int>): this(pay,null,PermissionStrings.defaults)
    constructor(pay: Pair<Int, Int>, spawnpos: Vec3d?): this(pay,spawnpos, PermissionStrings.defaults)

    override fun isTruthy(): Boolean = true

    fun getRoomIndex(): Pair<Int, Int> = pay
    fun getRoomValue(): Room {
        val storage = HexxyDimensions.STORAGE.get()
        val room = storage.all[pay.first]
        room.keyCheck(pay.second)
        return room
    }

    fun getTeleportPosition(): Vec3d {
        val room = getRoomValue()
        return if (spawnpos != null) {
            Vec3d(
                room.getX().toDouble(),
                room.height.toDouble(),
                room.getY().toDouble()
            ).add(spawnpos)
        } else {
            Vec3d(
                (room.getX().toDouble() + (room.getW().toDouble()/2)),
                0.0,
                (room.getY().toDouble() + (room.getH().toDouble()/2))
            )
        }
    }

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
        nbt.putBoolean("doOffset",spawnpos != null)
        if (spawnpos != null) {
            nbt.putDouble("ox",spawnpos.x)
            nbt.putDouble("oy",spawnpos.y)
            nbt.putDouble("oz",spawnpos.z)
        }
        nbt.putByteArray("permissions",permissions.map { (if (it) {1} else {0}).toByte() })
        return nbt
    }

    fun copy(pay: Pair<Int,Int> = this.pay, spawnpos: Vec3d? = this.spawnpos, permissions: List<Boolean> = this.permissions) = RoomIota(pay,spawnpos,permissions)

    companion object {
        val TYPE = RoomIotaType()
    }
}