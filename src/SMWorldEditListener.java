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

/**
 * Event listener for Hey0's server mod plugin.
 *
 * @author sk89q
 */
public class SMWorldEditListener extends PluginListener {
    /**
     *
     * @param player
     */
    @Override
    public void onDisconnect(Player player) {
        WorldEdit worldEdit = WorldEdit.getInstance();
        worldEdit.removeSession(new SMWorldEditPlayer(player));
    }

    /**
     * Called on right click.
     *
     * @param modPlayer
     * @param blockPlaced
     * @param blockClicked
     * @param itemInHand
     * @return false if you want the action to go through
     */
    @Override
    public boolean onBlockCreate(Player modPlayer, Block blockPlaced,
            Block blockClicked, int itemInHand) {
        WorldEdit worldEdit = WorldEdit.getInstance();
        WorldEditPlayer player = new SMWorldEditPlayer(modPlayer);
        
        if (itemInHand != 271) { return false; }
        if (!canUseCommand(modPlayer, "//pos2")) { return false; }

        WorldEditSession session = worldEdit.getSession(player);

        if (session.isToolControlEnabled()) {
            Vector cur = Vector.toBlockPoint(blockClicked.getX(),
                                           blockClicked.getY(),
                                           blockClicked.getZ());

            session.setPos2(cur);
            player.print("Second edit position set.");

            return true;
        }

        return false;
    }

    /**
     * Called on left click.
     *
     * @param modPlayer
     * @param blockClicked
     * @param itemInHand
     * @return false if you want the action to go through
     */
    @Override
    public boolean onBlockDestroy(Player modPlayer, Block blockClicked) {
        if (!canUseCommand(modPlayer, "//pos1")
                && !canUseCommand(modPlayer, "//")) { return false; }

        WorldEdit worldEdit = WorldEdit.getInstance();
        WorldEditPlayer player = new SMWorldEditPlayer(modPlayer);
        WorldEditSession session = worldEdit.getSession(player);

        if (player.getItemInHand() == 271) {
            if (session.isToolControlEnabled()) {
                Vector cur = Vector.toBlockPoint(blockClicked.getX(),
                                               blockClicked.getY(),
                                               blockClicked.getZ());

                // Bug workaround
                if (cur.getBlockX() == 0 && cur.getBlockY() == 0
                        && cur.getBlockZ() == 0) {
                    return false;
                }

                try {
                    if (session.getPos1().equals(cur)) {
                        return false;
                    }
                } catch (IncompleteRegionException e) {
                }

                session.setPos1(cur);
                player.print("First edit position set.");

                return true;
            }
        } else if (player.isHoldingPickAxe()) {
            if (session.hasSuperPickAxe()) {
                Vector pos = new Vector(blockClicked.getX(),
                        blockClicked.getY(), blockClicked.getZ());
                if (WorldEdit.getServer().getBlockType(pos) == 7
                        && !canUseCommand(modPlayer, "/worldeditbedrock")) {
                    return true;
                }
                
                WorldEdit.getServer().setBlockType(pos, 0);

                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param modPlayer
     * @param split
     * @return whether the command was processed
     */
    @Override
    public boolean onCommand(Player modPlayer, String[] split) {
        WorldEdit worldEdit = WorldEdit.getInstance();
        WorldEditPlayer player = new SMWorldEditPlayer(modPlayer);
        
        try {
            // Legacy /, command
            if (split[0].equals("/,")) {
                split[0] = "//";
            }
            
            if (worldEdit.getCommands().containsKey(split[0].toLowerCase())) {
                if (canUseCommand(modPlayer, split[0])) {
                    WorldEditSession session = worldEdit.getSession(player);
                    EditSession editSession =
                            new EditSession(session.getBlockChangeLimit());
                    editSession.enableQueue();

                    try {
                        return worldEdit.performCommand(player, session, editSession, split);
                    } finally {
                        session.remember(editSession);
                        editSession.flushQueue();
                    }
                }
            }

            return false;
        } catch (Throwable e) {
            Operation.printException(player, e);
        }

        return true;
    }

    /**
     * Checks to see if the player can use a command or /worldedit.
     * 
     * @param player
     * @param command
     * @return
     */
    private boolean canUseCommand(Player player, String command) {
        return player.canUseCommand(command.replace("air", ""))
                || player.canUseCommand("/worldedit");
    }
}