// $Id$
/*
 * WorldEdit
 * Copyright (C) 2010 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sk89q.util.StringUtil;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LogFormat;
import com.sk89q.worldedit.snapshots.SnapshotRepository;

/**
 * Configuration for CanaryMod.
 * 
 * @author sk89q
 */
public class CanaryConfiguration extends LocalConfiguration {
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("Minecraft.WorldEdit");
    
    /**
     * Properties file.
     */
    private PropertiesFile properties;
    
    /**
     * Construct the object.
     */
    public CanaryConfiguration() {
        properties = new PropertiesFile("worldedit.properties");
    }
    
    /**
     * Loads the configuration.
     */
    @Override
    @SuppressWarnings("LoggerStringConcat")
    public void load() {
        try {
            properties.load();
        } catch (IOException e) {
            logger.warning("worldedit.properties could not be loaded: "
                    + e.getMessage());
        }

        profile = properties.getBoolean("debug-profile", profile);
        wandItem = properties.getInt("wand-item", wandItem);
        defaultChangeLimit = Math.max(-1, properties.getInt(
                "default-max-blocks-changed", defaultChangeLimit));
        maxChangeLimit = Math.max(-1,
                properties.getInt("max-blocks-changed", maxChangeLimit));
        maxRadius = Math.max(-1, properties.getInt("max-radius", maxRadius));
        maxSuperPickaxeSize = Math.max(1, properties.getInt(
                "max-super-pickaxe-size", maxSuperPickaxeSize));
        registerHelp = properties.getBoolean("register-help", registerHelp);
        logCommands = properties.getBoolean("log-commands", logCommands);
        superPickaxeDrop = properties.getBoolean("super-pickaxe-drop-items",
                superPickaxeDrop);
        superPickaxeManyDrop = properties.getBoolean(
                "super-pickaxe-many-drop-items", superPickaxeManyDrop);
        noDoubleSlash = properties.getBoolean("no-double-slash", noDoubleSlash);
        useInventory = properties.getBoolean("use-inventory", useInventory);
        useInventoryOverride = properties.getBoolean("use-inventory-override",
                useInventoryOverride);
        maxBrushRadius = properties.getInt("max-brush-radius", maxBrushRadius);

        navigationWand = properties.getInt("navigation-wand-item", navigationWand);
        navigationWandMaxDistance = properties.getInt("navigation-wand-max-distance", navigationWandMaxDistance);

        scriptTimeout = properties.getInt("script-timeout", scriptTimeout);
        scriptsDir = properties.getString("scripts-dir", scriptsDir);

        saveDir = properties.getString("save-dir", saveDir);

        // Get disallowed blocks
        disallowedBlocks = new HashSet<Integer>();
        String defdisallowedBlocks = StringUtil.joinString(defaultDisallowedBlocks, ",", 0);
        for (String b : properties.getString("disallowed-blocks",
                defdisallowedBlocks).split(",")) {
            try {
                disallowedBlocks.add(Integer.parseInt(b));
            } catch (NumberFormatException e) {
            }
        }

        allowedDataCycleBlocks = new HashSet<Integer>();
        for (String b : properties.getString("allowed-data-cycle-blocks", "").split(",")) {
            try {
                allowedDataCycleBlocks.add(Integer.parseInt(b));
            } catch (NumberFormatException e) {
            }
        }

        LocalSession.MAX_HISTORY_SIZE = Math.max(0, properties.getInt("history-size", 15));
        LocalSession.EXPIRATION_GRACE = properties.getInt("history-expiration", 10) * 60 * 1000;

        String snapshotsDir = properties.getString("snapshots-dir", "");
        if (!snapshotsDir.trim().equals("")) {
            snapshotRepo = new SnapshotRepository(snapshotsDir);
        } else {
            snapshotRepo = null;
        }

        String type = properties.getString("shell-save-type", "").trim();
        shellSaveType = type.equals("") ? null : type;

        String logFile = properties.getString("log-file", "");
        if (!logFile.equals("")) {
            try {
                FileHandler handler = new FileHandler(logFile, true);
                handler.setFormatter(new LogFormat());
                logger.addHandler(handler);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not use log file " + logFile + ": "
                        + e.getMessage());
            }
        } else {
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }
        }
    }
}
