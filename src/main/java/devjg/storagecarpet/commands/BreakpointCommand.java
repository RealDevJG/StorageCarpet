package devjg.storagecarpet.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import devjg.storagecarpet.managers.BreakpointManager;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class BreakpointCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("breakpoint")
            .requires(source -> source.hasPermissionLevel(3))
            .then(literal("list").executes(BreakpointManager::listWorldBreakpoints))
            .then(literal("clear").executes(BreakpointManager::clearWorldBreakpoints))
            .then(literal("add")
                .then(argument("pos", BlockPosArgumentType.blockPos())
                    .executes(BreakpointManager::addBreakpoint)
                    .then(argument("condition", StringArgumentType.string())
                        .executes(BreakpointManager::addConditionalBreakpoint))))
            .then(literal("remove")
                .then(argument("id", IntegerArgumentType.integer())
                    .executes(BreakpointManager::removeBreakpoint))));
    }
}
