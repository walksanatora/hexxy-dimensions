package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import net.minecraft.text.Text
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomIota

class OpEstimateTime : Action {
    val argc = 1
     fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
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

    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val mutStack = image.stack.toMutableList()
        val stack = mutableListOf<Iota>()
        for (i in 0..<argc) stack.add(mutStack.removeLast())
        val push = execute(stack,env)
        push.forEach { mutStack.add(it) }
        return OperationResult(image.copy(mutStack), listOf(),continuation, HexEvalSounds.NORMAL_EXECUTE)
    }

}