package net.walksanator.hexdim.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import at.petrak.hexcasting.common.lib.HexItems
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Util

class MishapInvalidEnv : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = FrozenColorizer(
        HexItems.DYE_COLORIZERS[DyeColor.LIGHT_GRAY]!!.defaultStack, Util.NIL_UUID
    )

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text = Text.translatable(
        "hexdim.mishap.invalidenv"
    )

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        //noop
    }
}