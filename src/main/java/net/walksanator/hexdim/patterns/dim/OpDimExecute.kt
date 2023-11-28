package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import net.minecraft.text.Text
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.casting.HexDimComponents
import net.walksanator.hexdim.iotas.RoomIota
import net.walksanator.hexdim.mixin.MixinCastingEnvironment

class OpDimExecute(val activate: Boolean) : Action {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val stack = image.stack.toMutableList()
        val room = stack.removeLastOrNull() ?: throw MishapNotEnoughArgs(2,1)
        if (room.type != RoomIota.TYPE) {throw MishapInvalidIota(room,1, Text.literal("expected room iota"))}

        return exec((room as RoomIota).pay,env,image,continuation,stack)
    }

    private fun exec(roomIdx: Pair<Int,Int>, env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation, stack: MutableList<Iota>): OperationResult {
        val image2 = image.withUsedOp().copy(stack = stack)

        val storage = HexxyDimensions.STORAGE.get()
        val room = storage.all[roomIdx.first]
        if (!room.keyCheck(roomIdx.second)) { return OperationResult(image2, listOf(), continuation, HexEvalSounds.MISHAP) } //TODO: mishap invalid room

        if (activate) {
            val oldWorld = env.world
            (env as MixinCastingEnvironment).setWorld(storage.world)
            env.addExtension(HexDimComponents.VecInRange(oldWorld,room)) //we pass oldWorld here so that we can retrieve it if we de-activate
            env.addExtension(HexDimComponents.HasPermissionsAt(room))
        } else {
            val oldWorld = env.getExtension(HexDimComponents.VecInRange.KEY)!!.oldWorld
            env.removeExtension(HexDimComponents.VecInRange.KEY)
            env.removeExtension(HexDimComponents.HasPermissionsAt.KEY)
            (env as MixinCastingEnvironment).setWorld(oldWorld)
        }

        return OperationResult(image2, listOf(), continuation, HexEvalSounds.HERMES)

    }
}