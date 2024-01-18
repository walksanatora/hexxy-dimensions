package net.walksanator.hexdim.casting.mishap


import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.walksanator.hexdim.util.Room
import java.util.*

class MishapInvalidRoom(val room: Room,val isCarved: Boolean) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = FrozenPigment(ItemStack(
        HexItems.DYE_PIGMENTS[DyeColor.RED]
    ), UUID.randomUUID())

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text? {
        return if (!isCarved) {
            Text.literal("The room is not carved yet")
        } else {
            Text.literal("The room key is invalid")
        }
    }

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}