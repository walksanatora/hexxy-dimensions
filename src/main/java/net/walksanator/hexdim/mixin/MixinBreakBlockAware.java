package net.walksanator.hexdim.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.walksanator.hexdim.HexxyDimensions;
import net.walksanator.hexdim.casting.HexBreakBlockAware;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.OpBreakBlock$Spell")
public abstract class MixinBreakBlockAware {
    @Shadow @Final
    private BlockPos pos;

    protected MixinBreakBlockAware() {}

    @Inject(method = "cast(Lat/spetrak/hexcasting/api/casting/eval/CastingEnvironment;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER))
    private void hexdim$cast(CallbackInfo ci, @Local CastingEnvironment env, @Local BlockState blockState) {
        Block bl = blockState.getBlock();
        HexxyDimensions.INSTANCE.breakpoint_target();
        if (bl instanceof HexBreakBlockAware hbl) {
            hbl.onHexcastingBreak(env, this.pos,blockState);
        }
    }
}
