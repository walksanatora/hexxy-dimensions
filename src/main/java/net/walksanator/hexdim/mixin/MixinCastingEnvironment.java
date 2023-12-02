package net.walksanator.hexdim.mixin;


import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.walksanator.hexdim.mixin_interface.ICastingContext;
import net.walksanator.hexdim.util.Room;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CastingContext.class)
public class MixinCastingEnvironment implements ICastingContext {

    @Unique
    private ServerWorld world = null;
    @Unique
    private Room room = null;

    public void hexxy_dimensions$setWorld(ServerWorld nworld) {world = nworld;}
    public void hexxy_dimensions$setRoom(Room nroom) {
        room = nroom;
    }
    public boolean hexxy_dimensions$isModded() {return world != null;}

    @Inject(method = "getWorld()Lnet/minecraft/server/world/ServerWorld;", at = @At("RETURN"), cancellable = true)
    void hexdim$getWorld(CallbackInfoReturnable<ServerWorld> cir) {
        if (world != null) {
            cir.setReturnValue(world);
        }
    }

    @Inject(method = "isVecInRange(Lnet/minecraft/util/math/Vec3d;)Z", at = @At("RETURN"), cancellable = true)
    void hexdim$isVecInRange(Vec3d pos, CallbackInfoReturnable<Boolean> cir) {
        if (world != null) {
            boolean withinX = room.getX() <= pos.x && pos.x <= room.getX() + room.getW();
            boolean withinY = pos.x >= room.getHeight();
            boolean withinZ = room.getY() <= pos.z && pos.z <= room.getY() + room.getH();
            cir.setReturnValue(withinX && withinY && withinZ);
        }
    }

    @Inject(method = "isVecInWorld(Lnet/minecraft/util/math/Vec3d;)Z", at = @At("RETURN"), cancellable = true)
    void hexdim$isVecInWorld(Vec3d pos, CallbackInfoReturnable<Boolean> cir) {
        if (world != null) {
            boolean withinX = room.getX() <= pos.x && pos.x <= room.getX() + room.getW();
            boolean withinY = pos.x >= room.getHeight();
            boolean withinZ = room.getY() <= pos.z && pos.z <= room.getY() + room.getH();
            cir.setReturnValue(withinX && withinY && withinZ);
        }
    }
}
