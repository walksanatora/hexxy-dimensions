package net.walksanator.hexdim.mixin;

import at.petrak.hexcasting.api.mod.HexConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(HexConfig.ServerConfigAccess.class)
public abstract class MixinHexConfig {
    
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;)Ljava/util/List;"))
    private static List<String> addMeToConfig(List<String> old) {
        int size = old.size();
        List<String> now = new ArrayList<>(size + 1);
        now.add("hexdim:hexdim");
        return now;
    }
}
