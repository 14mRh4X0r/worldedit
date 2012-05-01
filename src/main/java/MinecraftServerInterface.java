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

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.util.TreeGenerator.TreeType;
import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.ReflectionFactory;

/**
 *
 * @author sk89q
 */
public class MinecraftServerInterface {
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("Minecraft.WorldEdit");
    /**
     * Random generator.
     */
    private static Random random = new Random();
    /**
     * Proxy for the tree generator.
     */
    private static MinecraftSetBlockProxy proxy;

    /**
     * Perform world generation at a location.
     *
     * @param pt
     * @return
     */
    private static boolean performWorldGen(EditSession editSession, Vector pt,
    		OWorldGenerator worldGen) {
        if (proxy == null) {
            try {
                proxy = createNoConstructor(MinecraftSetBlockProxy.class);
            } catch (Throwable t) {
                logger.log(Level.WARNING, "setBlock() proxy class failed to construct",
                        t);
                return false;
            }
        }
        proxy.setEditSession(editSession);

        OWorldGenerator gen = worldGen;
        return gen.a(proxy, random,
                pt.getBlockX(), pt.getBlockY() + 1, pt.getBlockZ());
    }    

    /**
     * Instantiate a class without calling its constructor.
     *
     * @param <T>
     * @param clazz
     * @return
     * @throws Throwable
     */
    @SuppressWarnings("rawtypes")
    private static <T> T createNoConstructor(Class<T> clazz) throws Throwable {
        try {
            ReflectionFactory factory = ReflectionFactory.getReflectionFactory();
            Constructor objectConstructor = Object.class.getDeclaredConstructor();
            Constructor c = factory.newConstructorForSerialization(
                clazz, objectConstructor
            );
            return clazz.cast(c.newInstance());
        } catch (Throwable e) {
            throw e;
        }
    }

    public static boolean generateTree(TreeType type, EditSession editSession, Vector pt) {
        OWorldGenerator treeGen;
        switch (type) {
            case BIG_TREE:
                treeGen = new OWorldGenBigTree(true);
                break;
            case BIRCH:
                treeGen = new OWorldGenForest(true);
                break;
            case BROWN_MUSHROOM:
                treeGen = new OWorldGenBigMushroom(0);
                break;
            case JUNGLE:
                treeGen = new OWorldGenHugeTrees(true, 10 + random.nextInt(20), 3, 3);
                break;
            case JUNGLE_BUSH:
                treeGen = new OWorldGenShrub(3, 0);
                break;
            case REDWOOD:
                treeGen = new OWorldGenTaiga2(true);
                break;
            case RED_MUSHROOM:
                treeGen = new OWorldGenBigMushroom(1);
                break;
            case SHORT_JUNGLE:
                treeGen = new OWorldGenTrees(true, 4 + random.nextInt(7), 3, 3, false);
                break;
            case SWAMP:
                treeGen = new OWorldGenSwamp();
                break;
            case TALL_REDWOOD:
                treeGen = new OWorldGenTaiga1();
                break;
            case TREE:
            default:
                treeGen = new OWorldGenTrees(true);
                break;
        }
        return performWorldGen(editSession, pt, treeGen);
    }
}
