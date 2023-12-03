package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.walksanator.hexdim.iotas.EntryIota
import net.walksanator.hexdim.iotas.RoomIota

class OpPasskey(private val acceptRelative: Boolean) : ConstMediaAction {
    override val argc: Int
        get() {return if (acceptRelative) {2} else {1}}
    override val isGreat: Boolean = true
    override fun execute(args: List<Iota>, env: CastingContext): List<Iota> {
        val iota = args[0]
        return if (acceptRelative) {
            if (iota.type != RoomIota.TYPE) { throw MishapInvalidIota(iota,0, Text.literal("Expected Room Iota")) }
            val pos = args[1]
            if (pos.type != Vec3Iota.TYPE) { throw MishapInvalidIota(iota,0, Text.literal("Expected Vec3 Iota"))}
            listOf(EntryIota((iota as RoomIota).pay,(pos as Vec3Iota).vec3))
        } else {
            if (iota.type != RoomIota.TYPE) { throw MishapInvalidIota(iota,0, Text.literal("Expected Room Iota"))}
            listOf((iota as RoomIota).downgradeToEntry())
        }
    }
}