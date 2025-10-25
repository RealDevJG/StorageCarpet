package devjg.storagecarpet.commands;

import carpet.utils.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class ExampleCommand
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(literal("testcommand").
                then(literal("first").
                        executes( (c)-> {
                            assert MinecraftClient.getInstance().player != null;
                            Messenger.m(c.getSource(), "b Server-Side Message");
                            c.getSource().sendMessage(Text.of("Client-Side Message"));
                            c.getSource().sendMessage(
                                Text.of("§c§lClient-Side Message (formatted)")
                            );
                            return 1;
                        })).
                then(literal("second").
                        executes( (c)-> printCommandExecutionData(c.getSource()))));
    }

    private static int printCommandExecutionData(ServerCommandSource source)
    {
        Messenger.m(source, "w Executed from world file: " + source.getServer().getSaveProperties().getLevelName());
        Messenger.m(source, "w Executed from world: " + source.getWorld().getRegistryKey().getValue());

        return 1;
    }
}
