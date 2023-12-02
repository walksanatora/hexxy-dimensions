package net.walksanator.hexdim.casting

import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.OperationResult
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs

interface VarMediaOutputAction : Action {
    val argc: Int

    fun execute(args: List<Iota>, env: CastingContext): CastResult


    override fun operate(
        continuation: at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation,
        stack: MutableList<Iota>,
        ravenmind: Iota?,
        ctx: CastingContext
    ): OperationResult {
        if (argc > stack.size)
            throw MishapNotEnoughArgs(argc, stack.size)
        val args = stack.takeLast(argc)
        repeat(argc) { stack.removeLast() }
        val spellResponse = this.execute(args,ctx)
        val sideEffects = mutableListOf<OperatorSideEffect>(OperatorSideEffect.ConsumeMedia(spellResponse.cost))
        stack.addAll(spellResponse.cast(ctx))
        return OperationResult(
            continuation,
            stack, ravenmind, sideEffects
        )
    }

    abstract class CastResult(val cost: Int, val particles: List<ParticleSpray>, opCount: Long) {
        abstract fun cast(env: CastingContext): List<Iota>
    }
}