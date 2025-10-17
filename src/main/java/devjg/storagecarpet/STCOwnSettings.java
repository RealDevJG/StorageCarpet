package devjg.storagecarpet;

import carpet.api.settings.Rule;

public class STCOwnSettings
{
    public enum Option
    {
        OPTION_A, OPTION_B, OPTION_C
    }

    @Rule(categories = "misc")
    public static int intSetting = 10;

    @Rule(options = {"foo", "bar", "baz"}, categories = "misc", strict = false)
    public static String stringSetting = "foo";

    /**
     * this is a test thing
     * to see if it shows up in
     * /carpet rules
     */
    @Rule(categories = "misc")
    public static Option optionSetting = Option.OPTION_A;

    @Rule(categories = "misc")
    public static boolean boolSetting;
}
