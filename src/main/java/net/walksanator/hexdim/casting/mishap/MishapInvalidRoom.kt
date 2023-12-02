package net.walksanator.hexdim.casting.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import at.petrak.hexcasting.common.lib.HexItems
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.walksanator.hexdim.util.Room
import java.util.*

class MishapInvalidRoom(val room: Room,val isCarved: Boolean) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = FrozenColorizer(ItemStack(
        HexItems.DYE_COLORIZERS.get(DyeColor.RED)
    ), UUID.randomUUID())

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return if (!isCarved) {
            Text.literal("The room is not carved yet")
        } else {
            Text.literal("The room key is invalid")
        }
    }

    override fun execute(env: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {}


}