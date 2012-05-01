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

import com.sk89q.worldedit.BiomeType;
import java.util.Locale;

public enum CanaryBiomeType implements BiomeType {

    OCEAN(OBiomeGenBase.b),
    PLAINS(OBiomeGenBase.c),
    DESERT(OBiomeGenBase.d),
    EXTREME_HILLS(OBiomeGenBase.e),
    FOREST(OBiomeGenBase.f),
    TAIGA(OBiomeGenBase.g),
    SWAMPLAND(OBiomeGenBase.h),
    RIVER(OBiomeGenBase.i),
    HELL(OBiomeGenBase.j),
    SKY(OBiomeGenBase.k),
    FROZEN_OCEAN(OBiomeGenBase.l),
    FROZEN_RIVER(OBiomeGenBase.m),
    ICE_PLAINS(OBiomeGenBase.n),
    ICE_MOUNTAINS(OBiomeGenBase.o),
    MUSHROOM_ISLAND(OBiomeGenBase.p),
    MUSHROOM_SHORE(OBiomeGenBase.q),
    BEACH(OBiomeGenBase.r),
    DESERT_HILLS(OBiomeGenBase.s),
    FOREST_HILLS(OBiomeGenBase.t),
    TAIGA_HILLS(OBiomeGenBase.u),
    SMALL_MOUNTAINS(OBiomeGenBase.v),
    JUNGLE(OBiomeGenBase.w),
    JUNGLE_HILLS(OBiomeGenBase.x);

    private OBiomeGenBase canaryBiome;

    private CanaryBiomeType(OBiomeGenBase biome) {
        this.canaryBiome = biome;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public OBiomeGenBase getCanaryBiome() {
        return canaryBiome;
    }
}
