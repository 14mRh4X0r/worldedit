// $Id$
/*
 * WorldEditLibrary
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

import com.sk89q.util.StringUtil;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.ServerInterface;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.WorldVector;
import com.sk89q.worldedit.bags.BlockBag;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.cui.CUIEvent;

/**
 *
 * @author sk89q
 */
public class HMPlayer extends LocalPlayer {
    /**
     * Stores the player.
     */
    private Player player;
    /**
     * World.
     */
    private HMWorld world;

    /**
     * Construct the object.
     * 
     * @param server
     * @param world
     * @param player
     */
    public HMPlayer(ServerInterface server, HMWorld world, Player player) {
        super(server);
        this.player = player;
        this.world = world;
    }

    /**
     * Get the ID of the item that the player is holding.
     *
     * @return
     */
    @Override
    public int getItemInHand() {
        return player.getItemInHand();
    }

    /**
     * Get the name of the player.
     *
     * @return String
     */
    @Override
    public String getName() {
        return player.getName();
    }

    /**
     * Get the player's view pitch.
     *
     * @return pitch
     */
    @Override
    public double getPitch() {
        return player.getPitch();
    }

    /**
     * Get the player's position.
     *
     * @return point
     */
    @Override
    public WorldVector getPosition() {
        return new WorldVector(world, player.getX(), player.getY(), player.getZ());
    }

    /**
     * Get the player's world.
     *
     * @return point
     */
    @Override
    public LocalWorld getWorld() {
        return world;
    }

    /**
     * Get the player's view yaw.
     *
     * @return yaw
     */
    @Override
    public double getYaw() {
        return player.getRotation();
    }

    /**
     * Gives the player an item.
     *
     * @param type
     * @param amt
     */
    @Override
    public void giveItem(int type, int amt) {
        player.giveItem(type, amt);
    }

    /**
     * Print a message.
     *
     * @param msg
     */
    @Override
    public void printRaw(String msg) {
        player.sendMessage(msg);
    }

    /**
     * Print a WorldEdit message.
     *
     * @param msg
     */
    @Override
    public void print(String msg) {
        player.sendMessage(Colors.LightPurple + msg);
    }

    /**
     * Print a WorldEdit error.
     *
     * @param msg
     */
    @Override
    public void printError(String msg) {
        player.sendMessage(Colors.Rose + msg);
    }

    /**
     * Move the player.
     *
     * @param pos
     * @param pitch
     * @param yaw
     */
    @Override
    public void setPosition(Vector pos, float pitch, float yaw) {
        Location loc = new Location();
        loc.x = pos.getX();
        loc.y = pos.getY();
        loc.z = pos.getZ();
        loc.rotX = (float) yaw;
        loc.rotY = (float) pitch;
        player.teleportTo(loc);
    }

    /**
     * Get a player's list of groups.
     * 
     * @return
     */
    @Override
    public String[] getGroups() {
        return player.getGroups();
    }
    
    /**
     * Checks if a player has permission.
     * 
     * @return
     */
    @Override
    public boolean hasPermission(String perm) {
        return player.canUseCommand("/" + perm);
    }
    
    /**
     * Get this player's block bag.
     */
    @Override
    public BlockBag getInventoryBlockBag() {
        return new HMPlayerInventoryBlockBag(player);
    }

    /**
     * @return the player
     */
    public Player getPlayerObject() {
        return player;
    }

    @Override
    public void printDebug(String msg) {
      player.sendMessage(Colors.LightGray + msg);
    }

    @Override
    public void dispatchCUIEvent(CUIEvent event) {
        String[] params = event.getParameters();

        if (params.length > 0) {
            player.sendMessage("\u00A75\u00A76\u00A74\u00A75" + event.getTypeId()
                    + "|" + StringUtil.joinString(params, "|"));
        } else {
            player.sendMessage("\u00A75\u00A76\u00A74\u00A75" + event.getTypeId());
        }
    }

    @Override
    public void dispatchCUIHandshake() {
        player.sendMessage("\u00A75\u00A76\u00A74\u00A75");
    }
}
