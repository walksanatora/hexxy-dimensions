package net.walksanator.hexdim.mixin_interface;

import net.minecraft.server.world.ServerWorld;
import net.walksanator.hexdim.util.Room;

public interface ICastingContext {
    void hexxy_dimensions$setWorld(ServerWorld world);
    void hexxy_dimensions$setRoom(Room world);
    boolean hexxy_dimensions$isModded();
}
