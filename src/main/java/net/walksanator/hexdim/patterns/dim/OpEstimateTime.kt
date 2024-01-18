package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomAccess

class OpEstimateTime : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val mut = args.toMutableList()
        val iota = mut.removeLast()
        if (iota !is RoomAccess) {
            throw MishapInvalidIota(iota,0, Text.translatable("iota.roomable"))
        }
        val casted = iota as RoomAccess
        val pay = casted.getRoomIndex()
        val storage = HexxyDimensions.STORAGE.get()
        val room = storage.all[pay.first]
        room.keyCheckNoCarveCheck(pay.second)
        val area = room.getW()*room.getH()*room.height
        val blocksLeft = area-room.blocksCarved
        mut.add(DoubleIota(blocksLeft.toDouble()))
        return mut
    }

}