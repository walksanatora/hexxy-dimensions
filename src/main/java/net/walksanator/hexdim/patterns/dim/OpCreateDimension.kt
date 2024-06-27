package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.walksanator.hexdim.casting.VarMediaOutputAction
import net.walksanator.hexdim.iotas.RoomIota
import net.walksanator.hexdim.HexxyDimensions

class OpCreateDimension : VarMediaOutputAction {
    override val argc: Int = 3

    override val causesBlindDiversion: Boolean = true
    override val isGreat: Boolean = true

    override fun execute(args: List<Iota>, env: CastingContext): VarMediaOutputAction.CastResult {
        if (args[0] is DoubleIota) {
            if (args[1] is DoubleIota) {
                if (args[2] is DoubleIota) {
                    val x = (args[0] as DoubleIota).double.toInt()
                    val y = (args[1] as DoubleIota).double.toInt()
                    val z = (args[2] as DoubleIota).double.toInt()
                    val cost = (x*y*z*MediaConstants.CRYSTAL_UNIT*3)/2
                    return Spell(x,y,z,cost,listOf(),1)
                }
                throw MishapInvalidIota(args[2],2, Text.literal("Excepted a double"))
            }
            throw MishapInvalidIota(args[1],1, Text.literal("Excepted a double"))
        }
        throw MishapInvalidIota(args[0],0, Text.literal("Excepted a double"))
    }

    class Spell(val x: Int, val y: Int, val z: Int, c: Int, p: List<ParticleSpray>, o: Long) : VarMediaOutputAction.CastResult(c.toLong(),p,o) {
        override fun cast(env: CastingContext): List<Iota> {
            val storage = HexxyDimensions.STORAGE.get()
            val room = storage.mallocRoom(Pair(x, z), y)
            if (room != null) {
                return listOf(RoomIota(Pair(storage.all.size-1,room.key!!)))
            }
            return listOf() //TODO: make a mishap for failing to allocate room...
        }

    }
}