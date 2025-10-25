package devjg.storagecarpet.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class Watchers {
    // Allows for a key location block pos to be world independent
    public record KeyLocation(RegistryKey<World> world, BlockPos blockPos) {}

    public record Hopper(KeyLocation keyLocation) {}

    public record Breakpoint(KeyLocation keyLocation, @Nullable String condition) {
        public Breakpoint(KeyLocation keyLocation) {
            this(keyLocation, null);
        }
    }
}
