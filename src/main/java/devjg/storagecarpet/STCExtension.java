package devjg.storagecarpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import carpet.utils.Messenger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class STCExtension implements CarpetExtension, ModInitializer
{
    public static void noop() { }
    private static SettingsManager mySettingManager;
    static
    {
        mySettingManager = new SettingsManager("1.0", "storagecarpet", "StorageCarpet");
        CarpetServer.manageExtension(new STCExtension());
    }

    @Override
    public void onGameStarted()
    {
        // let's /carpet handle our few simple settings
        CarpetServer.settingsManager.parseSettingsClass(STCSimpleSettings.class);
        CarpetServer.settingsManager.parseSettingsClass(STCOwnSettings.class);
        // Lets have our own settings class independent from carpet.conf
//        mySettingManager.parseSettingsClass(STCSimpleSettings.class);
//        mySettingManager.parseSettingsClass(STCOwnSettings.class);

        // set-up a snooper to observe how rules are changing in carpet
        CarpetServer.settingsManager.registerRuleObserver( (serverCommandSource, currentRuleState, originalUserTest) ->
        {
            if (currentRuleState.categories().contains("storagecarpet"))
            {
                Messenger.m(serverCommandSource,"gi Psssst... make sure not to change not to touch original carpet rules");
                // obviously you can change original carpet rules
            }
            else
            {
                Messenger.print_server_message(serverCommandSource.getServer(), "Ehlo everybody, "+serverCommandSource.getPlayer().getName().getString()+" is cheating...");
            }
        });
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, final CommandRegistryAccess commandBuildContext)
    {
        ExampleCommand.register(dispatcher);
    }

    @Override
    public SettingsManager extensionSettingsManager()
    {
        // this will ensure that our settings are loaded properly when world loads
        return mySettingManager;
    }

    @Override
    public String version() {
        return "storagecarpet";
    }

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(new STCExtension());
    }

    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        return STCExtension.getTranslationFromResourcePath(lang);
    }

    public static Map<String, String> getTranslationFromResourcePath(String lang)
    {
        InputStream langFile = STCExtension.class.getClassLoader().getResourceAsStream("assets/storagecarpet/lang/%s.json".formatted(lang));
        if (langFile == null) {
            // we don't have that language
            return Collections.emptyMap();
        }
        String jsonData;
        try {
            jsonData = IOUtils.toString(langFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        Gson gson = new GsonBuilder().setLenient().create(); // lenient allows for comments
        return gson.fromJson(jsonData, new TypeToken<Map<String, String>>() {}.getType());
    }
}
