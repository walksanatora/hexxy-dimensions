package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.text.Text
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.casting.VarMediaOutputAction
import net.walksanator.hexdim.iotas.RoomIota

class OpCreateDimension : VarMediaOutputAction {
    override val argc: Int = 3

    override fun execute(args: List<Iota>, env: CastingEnvironment): VarMediaOutputAction.CastResult {
        if (args[0] is DoubleIota) {
            if (args[1] is DoubleIota) {
                if (args[2] is DoubleIota) {
                    val x = (args[0] as DoubleIota).double.toInt()
                    val y = (args[1] as DoubleIota).double.toInt()
                    val z = (args[2] as DoubleIota).double.toInt()
                    val cost = x*y*z*MediaConstants.QUENCHED_SHARD_UNIT/2
                    return Spell(x,y,z,cost,listOf(),1)
                }
                throw MishapInvalidIota(args[2],2, Text.literal("Excepted a double"))
            }
            throw MishapInvalidIota(args[1],1, Text.literal("Excepted a double"))
        }
        throw MishapInvalidIota(args[0],0, Text.literal("Excepted a double"))
    }

    class Spell(val x: Int, val y: Int, val z: Int, c: Long,p: List<ParticleSpray>, o: Long) : VarMediaOutputAction.CastResult(c,p,o) {
        override fun cast(env: CastingEnvironment): List<Iota> {
            val storage = HexxyDimensions.STORAGE.get()
            val room = storage.mallocRoom(Pair(x, z), y)
            if (room != null) {
                return listOf(RoomIota(Pair(storage.all.size-1,room.key!!)))
            }
            return listOf() //TODO: make a mishap for failing to allocate room...
        }

    }
}