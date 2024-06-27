package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomIota

class OpEstimateTime : ConstMediaAction {
     override val argc = 1
     override fun execute(args: List<Iota>, env: CastingContext): List<Iota> {
        val mut = args.toMutableList()
        val iota = mut.removeLast()
        if (iota !is RoomIota) {
            throw MishapInvalidIota(iota,0, Text.translatable("iota.roomable"))
        }
        if (!iota.permissions[2]) {throw MishapInvalidIota(iota,1, Text.translatable("hexdim.iota.permissions.execute"))}
        val pay = iota.getRoomIndex()
        val storage = HexxyDimensions.STORAGE.get()
        val room = storage.all[pay.first]
        room.keyCheckNoCarveCheck(pay.second)
        val area = room.getW()*room.getH()*room.height
        val blocksLeft = area-room.blocksCarved
        mut.add(DoubleIota(blocksLeft.toDouble()))
        return mut
    }

}