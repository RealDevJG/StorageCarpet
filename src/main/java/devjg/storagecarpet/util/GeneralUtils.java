package devjg.storagecarpet.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.World;

import java.util.Optional;

public final class GeneralUtils {
    private GeneralUtils() {}

    public static Optional<String> getCurrentWorldId(MinecraftServer server) {
        if (server == null)
            return Optional.empty();

        return Optional.of(server.getSaveProperties().getLevelName());
    }

    public static RegistryKey<World> getCurrentDimension(CommandContext<ServerCommandSource> ctx) {
        return ctx.getSource().getWorld().getRegistryKey();
    }
}
