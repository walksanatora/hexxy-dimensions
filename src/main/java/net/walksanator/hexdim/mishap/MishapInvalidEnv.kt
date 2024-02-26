package net.walksanator.hexdim.mishap

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Util

class MishapInvalidEnv : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = FrozenPigment(
        HexItems.DYE_PIGMENTS[DyeColor.LIGHT_GRAY]!!.defaultStack, Util.NIL_UUID
    )

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text? = Text.translatable(
        "hexdim.mishap.invalidenv"
    )

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        //noop
    }
}