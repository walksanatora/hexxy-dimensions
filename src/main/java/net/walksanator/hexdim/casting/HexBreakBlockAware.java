package net.walksanator.hexdim.casting;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface HexBreakBlockAware {
    void onHexcastingBreak(CastingContext env, BlockPos pos, BlockState state);
}
