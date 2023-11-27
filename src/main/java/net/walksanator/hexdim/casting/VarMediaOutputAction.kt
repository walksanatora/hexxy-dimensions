package net.walksanator.hexdim.casting

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds

interface VarMediaOutputAction : Action {
    val argc: Int

    fun execute(args: List<Iota>, env: CastingEnvironment): CastResult


    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val stack = image.stack.toMutableList()
        if (argc > stack.size)
            throw MishapNotEnoughArgs(argc, stack.size)
        val args = stack.takeLast(argc)
        repeat(argc) { stack.removeLast() }
        val spellResponse = this.execute(args, env)
        val sideEffects = mutableListOf<OperatorSideEffect>(OperatorSideEffect.ConsumeMedia(spellResponse.cost))
        stack.addAll(spellResponse.cast(env))
        return OperationResult(
            image.copy(stack), sideEffects, continuation, HexEvalSounds.SPELL
        )
    }
    abstract class CastResult(val cost: Long, val particles: List<ParticleSpray>, opCount: Long)  {
        abstract fun cast(env: CastingEnvironment): List<Iota>
    }
}