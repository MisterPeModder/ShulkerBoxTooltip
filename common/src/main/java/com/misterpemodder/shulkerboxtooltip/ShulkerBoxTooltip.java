package com.misterpemodder.shulkerboxtooltip;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShulkerBoxTooltip {
    public static final String MOD_ID = "shulkerboxtooltip";
    public static final String MOD_NAME = "ShulkerBoxTooltip";
    public static final Logger LOGGER = LogManager.getFormatterLogger("ShulkerBoxTooltip");

    public static void init() {
        LOGGER.info(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
