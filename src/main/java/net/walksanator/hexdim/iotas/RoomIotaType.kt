package net.walksanator.hexdim.iotas

import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.utils.hasByteArray
import at.petrak.hexcasting.api.utils.styledWith
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import java.awt.Color

class RoomIotaType : IotaType<RoomIota>() {
    override fun deserialize(p0: NbtElement?, p1: ServerWorld?): RoomIota? {
        val nbt: NbtCompound = (p0 as NbtCompound)
        val room = Pair(
            nbt.getInt("idx"),
            nbt.getInt("key")
        )
        if (!nbt.hasByteArray("permissions")) {//legacy
            return RoomIota(room)
        }
        val offset = if (nbt.getBoolean("doOffset")) {
            Vec3d(
                nbt.getDouble("ox"),
                nbt.getDouble("oy"),
                nbt.getDouble("oz")
            )
        } else {null}
        return RoomIota(
            Pair(
                nbt.getInt("idx"),
                nbt.getInt("key")
            ),
            offset,
            nbt.getByteArray("permissions").map { it > 0 }
        )
    }

    override fun display(p0: NbtElement?): Text = Text.literal("Room %s [%s] %s".format(
        if ((p0 as NbtCompound).getBoolean("doOffset")){"(%s)".format(Vec3d(
            p0.getDouble("ox"),
            p0.getDouble("oy"),
            p0.getDouble("oz")
        ))}else{""},
        p0.getByteArray("permissions").map {it > 0}.toPermissionString(),
        p0.getInt("key").toString(16)
    )).styledWith(Style.EMPTY.withColor(color()))

    override fun color(): Int = Color(190, 100, 190).rgb
}

object PermissionStrings {
    val field = listOf("R","W","X")
    val defaults = listOf(true,true,true)
}
fun List<Boolean>.toPermissionString(): String {
    val PSFS = PermissionStrings.field.size
    val PSF = PermissionStrings.field
    val sb = StringBuilder()
    for (i in this.withIndex()) {
        if (i.index > PSFS) {break}
        if (i.value) {
            sb.append(
                PSF[i.index]
            )
        } else {sb.append('-')}
    }
    return sb.toString()
}

fun List<Boolean>.setPermision(bit: String, value: Boolean): List<Boolean> {
    val clone = mutableListOf<Boolean>()
    this.forEach { clone.add(it) }
    clone[PermissionStrings.field.indexOf(bit)] = value
    return clone
}