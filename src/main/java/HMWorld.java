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
import com.sk89q.worldedit.blocks.*;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.TreeGenerator.TreeType;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * World for CanaryMod.
 * 
 * @author sk89q
 */
public class HMWorld extends LocalWorld {
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("Minecraft.WorldEdit");
    /**
     * World.
     */
    private World world;

    public HMWorld(World world) {
        this.world = world;
    }
    
    /**
     * Set block type.
     *
     * @param pt
     * @param type
     * @return
     */
    @Override
    public boolean setBlockType(Vector pt, int type) {
        return world.setBlockAt(type, pt.getBlockX(), pt.getBlockY(),
                pt.getBlockZ());
    }
    
    /**
     * Get block type.
     *
     * @param pt
     * @return
     */
    @Override
    public int getBlockType(Vector pt) {
        return world.getBlockIdAt(pt.getBlockX(), pt.getBlockY(),
                pt.getBlockZ());
    }

    /**
     * Set block data.
     *
     * @param pt
     * @param data
     */
    @Override
    public void setBlockData(Vector pt, int data) {
        world.setBlockData(pt.getBlockX(), pt.getBlockY(),
                        pt.getBlockZ(), data);
    }

    /**
     * Get block data.
     *
     * @param pt
     * @return
     */
    @Override
    public int getBlockData(Vector pt) {
        return world.getBlockData(pt.getBlockX(), pt.getBlockY(),
                pt.getBlockZ());
    }
    
    /**
     * Set sign text.
     *
     * @param pt
     * @param text
     */
    public void setSignText(Vector pt, String[] text) {
        Sign signData = (Sign)world.getComplexBlock(
                pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
        if (signData == null) {
            return;
        }
        for (byte i = 0; i < 4; i++) {
            signData.setText(i, text[i]);
        }
        signData.update();
    }
    
    /**
     * Get sign text.
     *
     * @param pt
     * @return
     */
    public String[] getSignText(Vector pt) {
        Sign signData = (Sign)world.getComplexBlock(
                pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
        if (signData == null) {
            return new String[]{"", "", "", ""};
        }
        String[] text = new String[4];
        for (byte i = 0; i < 4; i++) {
            text[i] = signData.getText(i);
        }
        return text;
    }

    /**
     * Gets the contents of chests. Will return null if the chest does not
     * really exist or it is the second block for a double chest.
     *
     * @param pt
     * @return
     */
    public BaseItemStack[] getChestContents(Vector pt) {
        ComplexBlock cblock = world.getOnlyComplexBlock(
                pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());

        BaseItemStack[] items;
        Item[] nativeItems;

        if (cblock instanceof Chest) {
            Chest chest = (Chest)cblock;
            nativeItems = chest.getContents();
        } else {
            return null;
        }

        items = new BaseItemStack[nativeItems.length];

        for (byte i = 0; i < nativeItems.length; i++) {
            Item item = nativeItems[i];
            
            if (item != null) {
                items[i] = new BaseItemStack((short)item.getItemId(),
                        item.getAmount(), (short)item.getDamage());
            }
        }

        return items;
    }

    /**
     * Sets a chest slot.
     *
     * @param pt
     * @param contents
     * @return
     */
    public boolean setChestContents(Vector pt,
            BaseItemStack[] contents) {
        
        ComplexBlock cblock = world.getOnlyComplexBlock(
                pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());

        if (cblock instanceof Chest) {
            Chest chest = (Chest)cblock;
            Item[] nativeItems = new Item[contents.length];
            
            for (int i = 0; i < contents.length; i++) {
                BaseItemStack item = contents[i];
                
                if (item != null) {
                    Item nativeItem =
                        new Item(item.getType(), item.getAmount());
                    nativeItem.setDamage(item.getDamage());
                    nativeItems[i] = nativeItem;
                }
            }
            
            setContents(chest, nativeItems);
        }

        return false;
    }

    /**
     * Clear a chest's contents.
     * 
     * @param pt
     * @return
     */
    public boolean clearChest(Vector pt) {
        ComplexBlock cblock = world.getOnlyComplexBlock(
                pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());

        if (cblock instanceof Chest) {
            Chest chest = (Chest)cblock;
            chest.clearContents();
            chest.update();
            return true;
        }


        return false;
    }
    
    /**
     * Set the contents of an ItemArray.
     * 
     * @param itemArray
     * @param contents
     */
    private void setContents(ItemArray<?> itemArray, Item[] contents) {
        int size = contents.length;

        for (int i = 0; i < size; i++) {
            if (contents[i] == null) {
                itemArray.removeItem(i);
            } else {
                itemArray.setSlot(contents[i].getItemId(),
                        contents[i].getAmount(), contents[i].getDamage(), i);
            }
        }
    }

    /**
     * Set mob spawner mob type.
     *
     * @param pt
     * @param mobType
     */
    public void setMobSpawnerType(Vector pt, String mobType) {
        ComplexBlock cblock = world.getComplexBlock(
                pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());

        if (!(cblock instanceof MobSpawner)) {
            return;
        }

        MobSpawner mobSpawner = (MobSpawner)cblock;
        mobSpawner.setSpawn(mobType);
        mobSpawner.update();
    }

    /**
     * Drop an item.
     *
     * @param pt
     * @param type
     * @param count
     */
    public void dropItem(Vector pt, int type, int count) {
        world.dropItem(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ(),
                type, count);
    }

    /**
     * Drop an item.
     *
     * @param pt
     * @param type
     */
    public void dropItem(Vector pt, int type) {
        world.dropItem(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ(),
                type, 1);
    }
    
    /**
     * Kill mobs in an area.
     * 
     * @param origin
     * @param radius
     * @return
     */
    @Override
    public int killMobs(Vector origin, int radius) {
        return killMobs(origin, radius, false);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof HMWorld && world.equals(((HMWorld)other).world);
    }

    @Override
    public int hashCode() {
        return world.hashCode();
    }

    @Override
    public String getName() {
        return world.getType().toString();
    }

    @Override
    public void setBlockDataFast(Vector pt, int data) {
        world.getWorld().b(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ(),
                data);
    }

    @Override
    public boolean setTypeIdAndData(Vector pt, int type, int data) {
        return world.setBlockAt(type, pt.getBlockX(), pt.getBlockY(),
                    pt.getBlockZ())
                && world.setBlockData(pt.getBlockX(), pt.getBlockY(),
                    pt.getBlockZ(), data);
    }

    @Override
    public boolean setTypeIdAndDataFast(Vector pt, int type, int data) {
        return world.getWorld().a(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ(),
                type, data);
    }

    @Override
    public int getBlockLightLevel(Vector pt) {
        // TODO: bug CanaryMod devs about the return type
        return (int) world.getLightLevel(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
    }

    @Override
    public boolean regenerate(Region region, EditSession editSession) {
        BaseBlock[] history = new BaseBlock[16 * 16 * 128];

        for (Vector2D chunk : region.getChunks()) {
            Vector min = new Vector(chunk.getBlockX() * 16, 0, chunk.getBlockZ() * 16);

            // First save all the blocks inside
            for (int x = 0; x < 16; ++x) {
                for (int y = 0; y < 128; ++y) {
                    for (int z = 0; z < 16; ++z) {
                        Vector pt = min.add(x, y, z);
                        int index = y * 16 * 16 + z * 16 + x;
                        history[index] = editSession.getBlock(pt);
                    }
                }
            }

            try {
                regenerateChunk(chunk.getBlockX(), chunk.getBlockZ());
            } catch (Throwable t) {
                logger.log(Level.WARNING, null, t);
            }

            // Then restore
            for (int x = 0; x < 16; ++x) {
                for (int y = 0; y < 128; ++y) {
                    for (int z = 0; z < 16; ++z) {
                        Vector pt = min.add(x, y, z);
                        int index = y * 16 * 16 + z * 16 + x;

                        // We have to restore the block if it was outside
                        if (!region.contains(pt)) {
                            editSession.smartSetBlock(pt, history[index]);
                        } else { // Otherwise fool with history
                            editSession.rememberChange(pt, history[index],
                                    editSession.rawGetBlock(pt));
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean copyToWorld(Vector pt, BaseBlock block) {
        // Signs
        if (block instanceof SignBlock) {
            setSignText(pt, ((SignBlock)block).getText());
            return true;

        // Furnaces
        } else if (block instanceof FurnaceBlock) {
            ComplexBlock container = world.getOnlyComplexBlock(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
            if (container == null || !(container instanceof Furnace)) return false;
            Furnace canary = (Furnace)container;
            FurnaceBlock we = (FurnaceBlock)block;
            canary.setBurnTime(we.getBurnTime());
            canary.setCookTime(we.getCookTime());
            return setContainerBlockContents(pt, ((ContainerBlock)block).getItems());

        // Chests/dispenser
        } else if (block instanceof ContainerBlock) {
            return setContainerBlockContents(pt, ((ContainerBlock)block).getItems());

        // Mob spawners
        } else if (block instanceof MobSpawnerBlock) {
            ComplexBlock container = world.getOnlyComplexBlock(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
            if (container == null || !(container instanceof MobSpawner)) return false;
            MobSpawner canary = (MobSpawner) container;
            MobSpawnerBlock we = (MobSpawnerBlock) block;
            canary.setSpawn(we.getMobType());
            canary.setDelay(we.getDelay());
            return true;

        // Note block
        } else if (block instanceof com.sk89q.worldedit.blocks.NoteBlock) {
            ComplexBlock container = world.getOnlyComplexBlock(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
            if (container == null || !(container instanceof NoteBlock)) return false;
            NoteBlock canary = (NoteBlock) container;
            com.sk89q.worldedit.blocks.NoteBlock we = (com.sk89q.worldedit.blocks.NoteBlock)block;
            canary.setNote(we.getNote());
            return true;
        }

        return false;
    }

    @Override
    public boolean copyFromWorld(Vector pt, BaseBlock block) {
        // Signs
        if (block instanceof SignBlock) {
            ((SignBlock) block).setText(getSignText(pt));
            return true;
        
        // Furnaces
        } else if (block instanceof FurnaceBlock) {
            ComplexBlock canaryBlock = world.getOnlyComplexBlock(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
            if (canaryBlock == null || !(canaryBlock instanceof Furnace)) return false;
            Furnace canary = (Furnace) canaryBlock;
            FurnaceBlock we = (FurnaceBlock) block;
            we.setBurnTime(canary.getBurnTime());
            we.setCookTime(canary.getCookTime());
            ((ContainerBlock) block).setItems(getContainerBlockContents(pt));
            return true;

        // Chests/dispenser
        } else if (block instanceof ContainerBlock) {
            ((ContainerBlock) block).setItems(getContainerBlockContents(pt));
            return true;
        
        // Mob spawners
        } else if (block instanceof MobSpawnerBlock) {
            ComplexBlock canaryBlock = world.getOnlyComplexBlock(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
            if (canaryBlock == null || !(canaryBlock instanceof MobSpawner)) return false;
            MobSpawner canary = (MobSpawner) canaryBlock;
            MobSpawnerBlock we = (MobSpawnerBlock) block;
            we.setMobType(canary.getSpawn());
            try {
                we.setDelay((short) ((OTileEntityMobSpawner) getPrivateField("spawner", canary)).a);
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(HMWorld.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(HMWorld.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        
        // Note block
        } else if (block instanceof com.sk89q.worldedit.blocks.NoteBlock) {
            ComplexBlock canaryBlock = world.getOnlyComplexBlock(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
            if (canaryBlock == null || !(canaryBlock instanceof NoteBlock)) return false;
            NoteBlock canary = (NoteBlock) canaryBlock;
            com.sk89q.worldedit.blocks.NoteBlock we = (com.sk89q.worldedit.blocks.NoteBlock) block;
            we.setNote(canary.getNote());
        }
        
        return false;
    }

    @Override
    public boolean clearContainerBlockContents(Vector pt) {
        ComplexBlock block = world.getOnlyComplexBlock(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
        if (block == null || !(block instanceof BaseContainerBlock)) {
            return false;
        }

        BaseContainerBlock chest = (BaseContainerBlock) block;
        chest.clearContents();
        return true;
    }

    @Override
    public void dropItem(Vector pt, BaseItemStack item) {
        world.dropItem(pt.getX(), pt.getY(), pt.getZ(), item.getType(), item.getAmount(), item.getDamage());
    }

    @Override
    public int killMobs(Vector origin, double radius, int flags) {
        boolean killPets = (flags & KillFlags.PETS) != 0;
        boolean killNPCs = (flags & KillFlags.NPCS) != 0;
        boolean killAnimals = (flags & KillFlags.ANIMALS) != 0;
        boolean withLightning = (flags & KillFlags.WITH_LIGHTNING) != 0;
        boolean killGolems = (flags & KillFlags.GOLEMS) != 0;
        
        int killed = 0;
        double radiusSq = radius * radius;
        
        for (LivingEntity ent : world.getLivingEntityList()) {
            if (ent.isPlayer())
                continue;
            
            if (!killAnimals && ent.isAnimal())
                continue;
            
            if (!killPets && ent.getEntity() instanceof OEntityTamable && ((OEntityTamable) ent.getEntity()).u_()) //isTamed
                continue;
            
            if (!killGolems && ent.getEntity() instanceof OEntityGolem)
                continue;
            
            if (!killNPCs && ent.getEntity() instanceof OEntityPlayer)
                continue;
            
            if (radius < 0 || origin.distanceSq(new Vector(ent.getX(), ent.getY(), ent.getZ())) <= radiusSq) {
                if (withLightning) {
                    OEntityLightningBolt oelb = new OEntityLightningBolt(world.getWorld(), ent.getX(), ent.getY(), ent.getZ());
                    world.getWorld().b(oelb);
                }
                
                ent.destroy();
                killed++;
            }
        }
        
        return killed;
    }

    @Override
    public int removeEntities(EntityType type, Vector origin, int radius) {
        int num = 0;
        
        for (BaseEntity ent : world.getEntityList()) {
            Vector entPos = new Vector(ent.getX(), ent.getY(), ent.getZ());
            OEntity oent = ent.getEntity();
            if (radius != -1 && entPos.distance(origin) > radius)
                continue;
            
            switch (type) {
                case ARROWS:
                    if (oent instanceof OEntityArrow) {
                        ent.destroy();
                        num++;
                    }
                    break;
                case BOATS:
                    if (oent instanceof OEntityBoat) {
                        ent.destroy();
                        num++;
                    }
                    break;
                case ITEMS:
                    if (oent instanceof OEntityItem) {
                        ent.destroy();
                        num++;
                    }
                    break;
                case MINECARTS:
                    if (oent instanceof OEntityMinecart) {
                        ent.destroy();
                        num++;
                    }
                    break;
                case PAINTINGS:
                    if (oent instanceof OEntityPainting) {
                        ent.destroy();
                        num++;
                    }
                    break;
                case TNT:
                    if (oent instanceof OEntityTNTPrimed) {
                        ent.destroy();
                        num++;
                    }
                    break;
                case XP_ORBS:
                    if (oent instanceof OEntityXPOrb) {
                        ent.destroy();
                        num++;
                    }
                    break;
            }
            
        }
        return num;
    }

    private boolean setContainerBlockContents(Vector pt, BaseItemStack[] items) {
        ComplexBlock complex = world.getOnlyComplexBlock(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
        if (complex == null || !(complex instanceof BaseContainerBlock))
            return false;
        BaseContainerBlock container = (BaseContainerBlock) complex;

        for (int i = 0; i < container.getContentsSize(); i++) {
            BaseItemStack item = items[i];
            if (item != null)
                container.setSlot(item.getType(), item.getAmount(), item.getDamage(), i);
        }

        return true;
    }
    

    private BaseItemStack[] getContainerBlockContents(Vector pt) {
        
        ComplexBlock block = world.getOnlyComplexBlock(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
        if (block == null || !(block instanceof BaseContainerBlock)) {
            return new BaseItemStack[0];
        }
        
        BaseContainerBlock container = (BaseContainerBlock) block;
        int size = container.getContentsSize();
        BaseItemStack[] contents = new BaseItemStack[size];
        
        for (int i = 0; i < size; ++i) {
            Item canaryItem = container.getItemFromSlot(i);
            if (canaryItem != null) {
                contents[i] = new BaseItemStack(
                        canaryItem.getItemId(),
                        canaryItem.getAmount(), 
                (short) canaryItem.getDamage());
            }
        }
        
        return contents;
    }

    private boolean regenerateChunk(int x, int z) {
        unloadChunk(x, z);

        OChunkProviderServer cps = world.getWorld().G;

        OChunk chunk = null;

        try {
            ((Set) getPrivateField("b", cps)).remove(Long.valueOf(OChunkCoordIntPair.a(x, z)));

            if (getPrivateField("d", cps) == null) {
                chunk = (OChunk) getPrivateField("c", cps);
            } else {
                chunk = ((OIChunkProvider) getPrivateField("d", cps)).b(x, z);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not access private fields", e);
        }

        chunkLoadPostProcess(chunk, x, z);

        refreshChunk(x, z);

        return chunk != null;
    }

    private boolean unloadChunk(int x, int z) {

        OChunkProviderServer cps = world.getWorld().G;

        OChunk chunk = cps.b(x, z);

        try {
            ((Set) getPrivateField("b", cps)).remove(Long.valueOf(OChunkCoordIntPair.a(x, z)));
            ((OLongHashMap) getPrivateField("f", cps)).d(OChunkCoordIntPair.a(x, z));
            ((List) getPrivateField("g", cps)).remove(chunk);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not access private fields", e);
        }

        return true;
    }

    private static Object getPrivateField(String field, Object instance) throws NoSuchFieldException, IllegalAccessException {
        java.lang.reflect.Field f = instance.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(instance);
    }

    private void chunkLoadPostProcess(OChunk chunk, int x, int z) {
        if (chunk != null) {
            OChunkProviderServer cps = world.getWorld().G;
            try {
                ((OLongHashMap) getPrivateField("f", cps)).a(OChunkCoordIntPair.a(x, z), chunk);
                ((List) getPrivateField("g", cps)).add(chunk);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Could not access private fields", e);
            }

            chunk.c();
            chunk.d();

            if (!chunk.p && cps.a(x + 1, z + 1) && cps.a(x, z + 1) && cps.a(x + 1, z)) {
                cps.a(cps, x, z);
            }

            if (cps.a(x - 1, z) && !cps.b(x - 1, z).p && cps.a(x - 1, z + 1) && cps.a(x, z + 1) && cps.a(x - 1, z)) {
                cps.a(cps, x - 1, z);
            }

            if (cps.a(x, z - 1) && !cps.b(x, z - 1).p && cps.a(x + 1, z - 1) && cps.a(x, z - 1) && cps.a(x + 1, z)) {
                cps.a(cps, x, z - 1);
            }

            if (cps.a(x - 1, z - 1) && !cps.b(x - 1, z - 1).p && cps.a(x - 1, z - 1) && cps.a(x, z - 1) && cps.a(x - 1, z)) {
                cps.a(cps, x - 1, z - 1);
            }
        }
    }

    public boolean refreshChunk(int x, int z) {
        if (!world.isChunkLoaded(x << 4, 0, z << 4)) {
            return false;
        }

        int px = x << 4;
        int pz = z << 4;

        // If there are more than 10 updates to a chunk at once, it carries out the update as a cuboid
        // This flags 16 blocks in a line along the bottom for update and then flags a block at the opposite corner at the top
        // The cuboid that contains these 17 blocks covers the entire chunk
        // The server will compress the chunk and send it to all clients

        for (int xx = px; xx < (px + 16); xx++) {
            world.getWorld().h(xx, 0, pz);
        }
        world.getWorld().h(px, 127, pz + 15);

        return true;
    }

    @Override
    public void checkLoadedChunk(Vector pt) {
        if (!world.isChunkLoaded(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ()))
            world.loadChunk(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
    }

    @Override
    public boolean playEffect(Vector pt, int type, int data) {
        world.getWorld().f(type, pt.getBlockX(), pt.getBlockY(), pt.getBlockZ(), data);
        return true;
    }

    @Override
    public BiomeType getBiome(Vector2D pt) {
        OBiomeGenBase biome = world.getWorld().a(pt.getBlockX(), pt.getBlockZ());
        try {
            return CanaryBiomeType.valueOf(biome.y.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException exc) {
            return BiomeType.UNKNOWN;
        }
    }

    @Override
    public void setBiome(Vector2D pt, BiomeType biome) {
        if (!(biome instanceof CanaryBiomeType))
            return;
        
        // Blatantly "borrowed" from Bukkit
        OBiomeGenBase canaryBiome = ((CanaryBiomeType) biome).getCanaryBiome();
        if (this.world.isChunkLoaded(pt.getBlockX(), 0, pt.getBlockZ())) {
            OChunk chunk = this.world.getChunk(pt.getBlockX(), pt.getBlockZ()).chunk;

            if (chunk != null) {
                byte[] biomevals = chunk.l();
                biomevals[((pt.getBlockZ() & 0xF) << 4) | (pt.getBlockX() & 0xF)] = (byte)canaryBiome.M;
            }
        }
    }

    @Override
    public boolean generateTree(TreeType type, EditSession editSession, Vector pt) throws MaxChangedBlocksException {
        return MinecraftServerInterface.generateTree(type, editSession, pt);
    }
}
