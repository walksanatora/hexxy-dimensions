package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import net.minecraft.text.Text
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.casting.HexDimComponents
import net.walksanator.hexdim.iotas.RoomIota
import net.walksanator.hexdim.mixin.MixinCastingEnvironment
import java.util.*

class OpDimExecute(val activate: Boolean) : Action {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val stack = image.stack.toMutableList()
        val room = if (activate) {
            val room = stack.removeLastOrNull() ?: throw MishapNotEnoughArgs(1,0)
            if (room.type != RoomIota.TYPE) {throw MishapInvalidIota(room,1, Text.literal("expected room iota"))} //TODO: make this a translation string
            Optional.of((room as RoomIota).pay)
        } else {
            Optional.empty()
        }
        return exec(room,env,image,continuation,stack)
    }

    private fun exec(roomOpt: Optional<Pair<Int,Int>>, env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation, stack: MutableList<Iota>): OperationResult {
        val envEnabled = env.getExtension(HexDimComponents.VecInRange.KEY) != null
        if (activate) {
            if (envEnabled) {throw MishapDisallowedSpell()} // TODO: make mishap for allready in env
            val roomIdx = roomOpt.get()
            val storage = HexxyDimensions.STORAGE.get()
            val room = storage.all[roomIdx.first]

            room.keyCheck(roomIdx.second) // this throws a mishap if the key check fails

            val oldWorld = env.world
            env.addExtension(HexDimComponents.VecInRange(oldWorld,room)) //we pass oldWorld here so that we can retrieve it if we de-activate
            env.addExtension(HexDimComponents.HasPermissionsAt(room))
            (env as MixinCastingEnvironment).setWorld(storage.world)
        } else {
            if (!envEnabled) {throw MishapDisallowedSpell()} //TODO: make mishap for not in env
            val oldWorld = env.getExtension(HexDimComponents.VecInRange.KEY)!!.oldWorld
            env.removeExtension(HexDimComponents.VecInRange.KEY)
            env.removeExtension(HexDimComponents.HasPermissionsAt.KEY)
            (env as MixinCastingEnvironment).setWorld(oldWorld)
        }

        return OperationResult(image, listOf(), continuation, HexEvalSounds.HERMES)

    }
}