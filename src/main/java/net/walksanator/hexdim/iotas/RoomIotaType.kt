package net.walksanator.hexdim.iotas

import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.utils.styledWith
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Style
import net.minecraft.text.Text
import java.awt.Color

class RoomIotaType : IotaType<RoomIota>() {
    override fun deserialize(p0: NbtElement?, p1: ServerWorld?): RoomIota? {
        val nbt: NbtCompound = (p0 as NbtCompound)
        return RoomIota(
            Pair(
                nbt.getInt("idx"),
                nbt.getInt("key")
            )
        )
    }

    override fun display(p0: NbtElement?): Text = Text.literal("Pocket World %s".format(
        (p0 as NbtCompound).getInt("key").toString(16)
    )).styledWith(Style.EMPTY.withColor(color()))

    override fun color(): Int = Color(190, 100, 190).rgb
}