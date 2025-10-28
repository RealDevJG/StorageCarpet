package devjg.storagecarpet.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import devjg.storagecarpet.managers.BreakpointManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.DataCommandObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private static void onExecuteGet(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        BreakpointManager.updatedThisTick = false;
    }
}
