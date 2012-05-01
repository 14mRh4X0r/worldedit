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

import com.sk89q.worldedit.*;
import java.util.Map;

/**
 * The event listener for WorldEdit in hMod.
 *
 * @author sk89q
 */
public class HMWorldEditListener extends PluginListener {
    /**
     * Main WorldEdit controller.
     */
    private com.sk89q.worldedit.WorldEdit controller;
    /**
     * Configuration.
     */
    private LocalConfiguration config;
    /**
     * A copy of the server instance. This is where all world<->WorldEdit calls
     * will go through.
     */
    private ServerInterface server;
    
    /**
     * Constructs an instance.
     */
    public HMWorldEditListener() {
        server = new HMServerInterface(this);
        config = new CanaryConfiguration();
        controller = new com.sk89q.worldedit.WorldEdit(server, config);
    }
    
    /**
     *
     * @param player
     */
    @Override
    public void onDisconnect(Player player) {
        controller.markExpire(wrapPlayer(player));
    }

    /**
     * Called on arm swing.
     * 
     * @param player
     */
    @Override
    public void onArmSwing(Player player) {
        controller.handleArmSwing(wrapPlayer(player));
    }

    /**
     * Called on right click.
     *
     * @param player
     * @param blockClicked
     * @param itemInHand
     * @return false if you want the action to go through
     */
    @Override
    public boolean onBlockRightClick(Player player, Block blockClicked, 
            Item itemInHand) {
        WorldVector pos = new WorldVector(new HMWorld(player.getWorld()), blockClicked.getX(),
                blockClicked.getY(), blockClicked.getZ());
        return controller.handleBlockRightClick(wrapPlayer(player), pos);
    }

    @Override
    public boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
        return controller.handleRightClick(wrapPlayer(player));
    }

    /**
     * Called on left click.
     *
     * @param player
     * @param blockClicked
     * @return false if you want the action to go through
     */
    @Override
    public boolean onBlockDestroy(Player player, Block blockClicked) {
        WorldVector pos = new WorldVector(new HMWorld(player.getWorld()), blockClicked.getX(),
                blockClicked.getY(), blockClicked.getZ());
        return controller.handleBlockLeftClick(wrapPlayer(player), pos);
    }

    /**
     *
     * @param player
     * @param split
     * @return whether the command was processed
     */
    @Override
    public boolean onCommand(Player player, String[] split) {
        return controller.handleCommand(wrapPlayer(player), split);
    }

    /**
     * Loads the configuration.
     */
    public void loadConfiguration() {
        config.load();
    }

    /**
     * Register commands with help.
     */
    public void registerCommands() {
        if (config.registerHelp) {
            for (Map.Entry<String,String> entry : controller.getCommands().entrySet()) {
                etc.getInstance().addCommand(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * De-register commands.
     */
    public void deregisterCommands() {
        for (String key : controller.getCommands().keySet()) {
            etc.getInstance().removeCommand(key);
        }
    }
    
    /**
     * Clear sessions.
     */
    public void clearSessions() {
        controller.clearSessions();
    }

    /**
     * Gets the WorldEditLibrary session for a player. Used for the bridge.
     *
     * @param player
     * @return
     */
    public LocalSession _bridgeSession(Player player) {
        return controller.getSession(wrapPlayer(player));
    }
    
    /**
     * Wrap a hMod player for WorldEdit.
     * 
     * @param player
     * @return
     */
    private LocalPlayer wrapPlayer(Player player) {
        return new HMPlayer(server, new HMWorld(player.getWorld()), player);
    }
}
