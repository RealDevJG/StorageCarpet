package devjg.storagecarpet;

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
                        executes( (c)-> listSettings(c.getSource()))));
    }

    private static int listSettings(ServerCommandSource source)
    {
        Messenger.m(source, "w Here is all the settings we manage:");
        Messenger.m(source, "w Own stuff:");
        Messenger.m(source, "w  - boolean: "+ STCOwnSettings.boolSetting);
        Messenger.m(source, "w  - string: "+ STCOwnSettings.stringSetting);
        Messenger.m(source, "w  - int: "+ STCOwnSettings.intSetting);
        Messenger.m(source, "w  - enum: "+ STCOwnSettings.optionSetting);
        Messenger.m(source, "w Carpet Managed:");
        Messenger.m(source, "w  - makarena: "+ STCSimpleSettings.makarena);
        Messenger.m(source, "w  - useless numerical setting: "+ STCSimpleSettings.uselessNumericalSetting);
        return 1;
    }
}
