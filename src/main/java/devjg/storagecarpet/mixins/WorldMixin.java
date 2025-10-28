package devjg.storagecarpet.mixins;

import devjg.storagecarpet.managers.BreakpointManager;
import devjg.storagecarpet.util.GeneralUtils;
import devjg.storagecarpet.util.Watchers;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.text.Text.*;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow
    public abstract boolean isClient();

    @Inject(method = "markDirty(Lnet/minecraft/util/math/BlockPos;)V", at = @At("HEAD"))
    private void onMarkDirty(BlockPos pos, CallbackInfo ci) {
        if (!this.isClient()) {
            MinecraftServer server = ((ServerWorld)(Object)this).getServer();
            String worldId = GeneralUtils.getCurrentWorldId(server).orElse(null);

            if (worldId == null)
                return;

            List<Watchers.Breakpoint> breakpoints = BreakpointManager.worldBreakpoints.get(worldId);
            if (breakpoints == null)
                return;

            boolean matches = breakpoints.stream().anyMatch(bp -> bp.keyLocation().blockPos().equals(pos));
            if (matches) {
                if (!BreakpointManager.updatedThisTick)
                    MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(of(pos.toShortString())); // Freeze game here on update

                BreakpointManager.updatedThisTick = true;
            }
        }
    }
}
