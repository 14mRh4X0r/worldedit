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
import com.sk89q.worldedit.BiomeTypes;
import com.sk89q.worldedit.UnknownBiomeTypeException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CanaryBiomeTypes implements BiomeTypes {

    public CanaryBiomeTypes() {
    }

    @Override
    public boolean has(String name) {
        try {
            CanaryBiomeType.valueOf(name.toUpperCase(Locale.ENGLISH));
            return true;
        } catch (IllegalArgumentException exc) {
            return false;
        }
    }

    @Override
    public BiomeType get(String name) throws UnknownBiomeTypeException {
        try {
            return CanaryBiomeType.valueOf(name.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException exc) {
            throw new UnknownBiomeTypeException(name);
        }
    }

    @Override
    public List<BiomeType> all() {
        return Arrays.<BiomeType>asList(CanaryBiomeType.values());
    }

}
