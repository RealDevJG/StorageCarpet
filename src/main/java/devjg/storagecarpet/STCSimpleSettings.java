package devjg.storagecarpet;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import carpet.api.settings.Validators;
import carpet.utils.Messenger;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import static carpet.api.settings.RuleCategory.CREATIVE;

public class STCSimpleSettings
{
    private static class CheckValue extends Validator<Integer>
    {
        @Override
        public Integer validate(@Nullable ServerCommandSource source, CarpetRule<Integer> changingRule, Integer newValue, String userInput) {
            Messenger.m(source, "rb Congrats, you just changed a setting to "+newValue);
            return newValue < 20000000 ? newValue : null;
        }
    }

    @Rule(
        options = {"32768", "250000", "1000000"},
        validators = {Validators.NonNegativeNumber.class, CheckValue.class},
        categories = {CREATIVE, "storagecarpet"}
    )
    public static int uselessNumericalSetting = 32768;

    /**
     * You can define your own catergories. It makes sense to create new category for all settings in your mod.
     */
    @Rule(categories = {"fun", "storagecarpet"})
    public static boolean makarena = false;
}
