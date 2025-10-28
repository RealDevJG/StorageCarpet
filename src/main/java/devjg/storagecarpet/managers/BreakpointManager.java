package devjg.storagecarpet.managers;

import carpet.utils.Messenger;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import devjg.storagecarpet.util.GeneralUtils;
import devjg.storagecarpet.util.Watchers;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO: Make #60A5FA a static final in some util/constants file
public final class BreakpointManager {
    // String is worldId
    public static final Map<String, List<Watchers.Breakpoint>> worldBreakpoints = new HashMap<>();
    public static boolean updatedThisTick = false;

    public static int addBreakpoint(CommandContext<ServerCommandSource> ctx) {
        BlockPos blockPos = ctx.getArgument("pos", DefaultPosArgument.class).toAbsoluteBlockPos(ctx.getSource());

        return createBreakpoint(ctx, blockPos, null)
            .map(breakpointId -> {
                Messenger.m(ctx.getSource(), "l Created breakpoint ", "#60A5FA [%d]".formatted(breakpointId));
                return 1;
            }).orElse(0);
    }

    public static int addConditionalBreakpoint(CommandContext<ServerCommandSource> ctx) {
        BlockPos blockPos = ctx.getArgument("pos", DefaultPosArgument.class).toAbsoluteBlockPos(ctx.getSource());
        String condition = ctx.getArgument("condition", String.class);

        return createBreakpoint(ctx, blockPos, condition)
            .map(breakpointId -> {
                Messenger.m(ctx.getSource(), "l Created breakpoint ", "#60A5FA [%d] ".formatted(breakpointId), "l with condition ", "#60A5FA [%s]".formatted(condition));
                return 1;
            }).orElse(0);
    }

    private static Optional<Integer> createBreakpoint(CommandContext<ServerCommandSource> ctx, BlockPos blockPos, @Nullable String condition) {
        Optional<String> worldIdOptional = GeneralUtils.getCurrentWorldId((ctx.getSource().getServer()));

        if (worldIdOptional.isEmpty())
            return Optional.empty();

        String worldId = worldIdOptional.get();
        var keyLocation = new Watchers.KeyLocation(GeneralUtils.getCurrentDimension(ctx), blockPos);
        var breakpoint = (condition == null) ? new Watchers.Breakpoint(keyLocation) : new Watchers.Breakpoint(keyLocation, condition);

        worldBreakpoints
            .computeIfAbsent(worldId, k -> new ArrayList<>())
            .add(breakpoint);

        // Newest breakpoint ID (1-indexed for user readability, programming logic should -1 to be 0-indexed)
        return Optional.of(worldBreakpoints.get(worldId).size());
    }

    private static void sendBreakpointEntry(ServerCommandSource source, int index, Watchers.Breakpoint bp) {
        BlockPos pos = bp.keyLocation().blockPos();
        RegistryKey<World> worldKey = bp.keyLocation().world();
        ServerWorld world = source.getServer().getWorld(worldKey);

        if (world == null) {
            Messenger.m(source, "r World ", "#60A5FA [%s] ".formatted(worldKey.getValue()), "r doesn't exist");
            return;
        }

        String id = "[%s]".formatted(index + 1);
        String block = "[%s]".formatted(world.getBlockState(pos).getBlock().getName().getString());
        String coords = pos.toShortString();
        String location = "[%s, %s]".formatted(coords, worldKey.getValue());
        String condition = (bp.condition() == null) ? "[any block update]" : "[%s]".formatted(bp.condition());

        // clickable teleport command
        String tpCommand = "!/execute in %s run tp @s %d %d %d".formatted(worldKey.getValue(), pos.getX(), pos.getY(), pos.getZ());

        Messenger.m(source,
            "g   • ID: ", "#60A5FA " + id,
            "g \n    Block: ", "#60A5FA " + block,
            "g \n    Location: ", Messenger.c("#60A5FA " + location, tpCommand, "^w Click to teleport"),
            "g \n    Condition: ", "#60A5FA " + condition
        );
    }

    public static int listWorldBreakpoints(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        Optional<String> worldIdOptional = GeneralUtils.getCurrentWorldId(source.getServer());

        if (worldIdOptional.isEmpty())
            return 0;

        String worldId = worldIdOptional.get();
        List<Watchers.Breakpoint> breakpoints = worldBreakpoints.get(worldId);

        if (breakpoints == null || breakpoints.isEmpty()) {
            Messenger.m(source, "r There are no breakpoints active in this world");
            return 1;
        }

        Messenger.m(source, "g Active breakpoints in this server:");
        for (int i = 0; i < breakpoints.size(); ++i)
            sendBreakpointEntry(source, i, breakpoints.get(i));

        return 1;
    }

    public static int clearWorldBreakpoints(CommandContext<ServerCommandSource> ctx) {
        Optional<String> worldIdOptional = GeneralUtils.getCurrentWorldId(ctx.getSource().getServer());

        if (worldIdOptional.isEmpty())
            return 0;

        String worldId = worldIdOptional.get();
        worldBreakpoints.remove(worldId);

        Messenger.m(ctx.getSource(), "l Cleared all breakpoints in this server");

        return 1;
    }

    // TODO refactor
    public static int removeBreakpoint(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        Optional<String> worldIdOptional = GeneralUtils.getCurrentWorldId(source.getServer());

        if (worldIdOptional.isEmpty())
            return 0;

        String worldId = worldIdOptional.get();
        List<Watchers.Breakpoint> breakpoints = worldBreakpoints.get(worldId);

        int id = IntegerArgumentType.getInteger(ctx, "id");
        int index = id - 1;

        if (breakpoints == null || index < 0 || index >= breakpoints.size()) {
            Messenger.m(source, "r Invalid breakpoint: ", "#60A5FA [%s]".formatted(id));
            return 0;
        }

        Watchers.Breakpoint removedBreakpoint = breakpoints.remove(index);
        BlockPos pos = removedBreakpoint.keyLocation().blockPos();
        RegistryKey<World> worldKey = removedBreakpoint.keyLocation().world();
        ServerWorld world = source.getServer().getWorld(worldKey);

        if (world == null) {
            Messenger.m(source, "r World ", "#60A5FA [%s] ".formatted(worldKey.getValue()), "r doesn't exist");
            return 0;
        }

        String tpCommand = "/execute in %s run tp @s %d %d %d".formatted(worldKey.getValue(), pos.getX(), pos.getY(), pos.getZ());

        String block = "[%s]".formatted(world.getBlockState(pos).getBlock().getName().getString());
        String coords = pos.toShortString();
        String location = "[%s, %s]".formatted(coords, worldKey.getValue());
        String condition = (removedBreakpoint.condition() == null) ? "[any block update]" : "[%s]".formatted(removedBreakpoint.condition());

        Text hoverText = Messenger.c(
            "w Click to teleport here",
            "g \nBlock: ", "#60A5FA " + block,
            "g \nLocation: ", "#60A5FA " + location,
            "g \nCondition: ", "#60A5FA " + condition
        );

        MutableText clickableId = Text.literal("[" + id + "]").setStyle(
            Messenger.parseStyle("#60A5FA")
                .withClickEvent(new ClickEvent.RunCommand(tpCommand))
                .withHoverEvent(new HoverEvent.ShowText(hoverText))
        );

        Messenger.m(source,
            "g Removed breakpoint ",
            clickableId
        );

        return 1;
    }
}
