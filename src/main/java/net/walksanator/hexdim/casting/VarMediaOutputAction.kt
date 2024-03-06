package net.walksanator.hexdim.casting

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
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
        val sideEffects = mutableListOf(
                OperatorSideEffect.ConsumeMedia(spellResponse.cost),
                OperatorSideEffect.AttemptSpell(spellResponse),
            )
        sideEffects.addAll(
            spellResponse.particles.map { OperatorSideEffect.Particles(it) }
        )
        return OperationResult(
            image.copy(stack), sideEffects, continuation, HexEvalSounds.SPELL
        )
    }
    abstract class CastResult(val cost: Long, val particles: List<ParticleSpray>): RenderedSpell {
        override fun cast(env: CastingEnvironment) {}//ignored (tf2 crit sound)

        /**
         * runs the contents of this spell (can mishap)
         * @return the iotas to append to the stack
         */
        abstract fun run(env: CastingEnvironment): List<Iota>
        override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
            val append = run(env)
            val stack = image.stack.toMutableList()
            append.forEach { stack.add(it) }
            return image.copy(stack)
        }

    }
}