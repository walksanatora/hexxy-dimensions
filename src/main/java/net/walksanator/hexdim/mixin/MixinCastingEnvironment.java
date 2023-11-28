package net.walksanator.hexdim.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CastingEnvironment.class)
public interface MixinCastingEnvironment {
    @Mutable @Accessor("world")
    void setWorld(ServerWorld world);

}
