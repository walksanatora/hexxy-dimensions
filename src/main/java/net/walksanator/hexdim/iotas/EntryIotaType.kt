package net.walksanator.hexdim.iotas

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.styledWith
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import java.awt.Color

class EntryIotaType : IotaType<EntryIota>() {
    override fun deserialize(p0: NbtElement?, p1: ServerWorld?): EntryIota? {
        val nbt: NbtCompound = (p0 as NbtCompound)
        return if (nbt.getBoolean("doOffset")){
            EntryIota(
                Pair(
                    nbt.getInt("idx"),
                    nbt.getInt("key")
                ),
                Vec3d(
                    nbt.getDouble("ox"),
                    nbt.getDouble("oy"),
                    nbt.getDouble("oz")
                )
            )
        } else {
            EntryIota(
                Pair(
                    nbt.getInt("idx"),
                    nbt.getInt("key")
                )
            )
        }
    }

    override fun display(p0: NbtElement?): Text {
       return Text.literal("Pocket World Access %s %s".format(
            (p0 as NbtCompound).getInt("key").toString(16),
            if (p0.getBoolean("doOffset")) {
                "(%s,%s,%s)".format(
                    p0.getDouble("ox"),
                    p0.getDouble("oy"),
                    p0.getDouble("oz")
                )
            } else { "" })
        ).styledWith(Style.EMPTY.withColor(color()))
    }

    override fun color(): Int = Color(110, 190, 110).rgb
}