package net.walksanator.hexdim.casting;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface HexBreakBlockAware {
    void onHexcastingBreak(CastingEnvironment env, BlockPos pos, BlockState state);
}
