package devjg.storagecarpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import devjg.storagecarpet.commands.BreakpointCommand;
import devjg.storagecarpet.commands.ExampleCommand;
import net.fabricmc.api.ModInitializer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class STCExtension implements CarpetExtension, ModInitializer
{
    public static String MOD_ID = "storagecarpet";

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(new STCExtension());
    }

    @Override
    public void onGameStarted()
    {
        CarpetServer.settingsManager.parseSettingsClass(STCSimpleSettings.class);
        CarpetServer.settingsManager.parseSettingsClass(STCOwnSettings.class);
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, final CommandRegistryAccess commandBuildContext)
    {
        ExampleCommand.register(dispatcher);
        BreakpointCommand.register(dispatcher);
    }

    @Override
    public String version() {
        return MOD_ID;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        return STCExtension.getTranslationFromResourcePath(lang);
    }

    public static Map<String, String> getTranslationFromResourcePath(String lang)
    {
        String langFilePath = "assets/" + MOD_ID + "/lang/%s.json".formatted(lang);

        try (InputStream langFile = STCExtension.class.getClassLoader().getResourceAsStream(langFilePath)) {
            if (langFile == null)
                return Collections.emptyMap();

            String jsonData = new String(langFile.readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = new GsonBuilder().create();

            return gson.fromJson(jsonData, new TypeToken<Map<String, String>>() {}.getType());
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}
