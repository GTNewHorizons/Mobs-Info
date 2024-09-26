/*
 * spotless:off
 * MobsInfo - Minecraft addon
 * Copyright (C) 2023-2024  kuba6000
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <https://www.gnu.org/licenses/>.
 * spotless:on
 */

package com.kuba6000.mobsinfo.api.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.IBossDisplayData;

import com.kuba6000.mobsinfo.api.LoaderReference;
import com.kuba6000.mobsinfo.mixin.late.EnderIO.ItemSoulVesselAccessor;

import crazypants.enderio.EnderIO;

public class EnderIOHelper {

    public static boolean canEntityBeCapturedWithSoulVial(Entity entity, String entityID) {
        if (!LoaderReference.EnderIO.isLoaded) return true;
        if (((ItemSoulVesselAccessor) EnderIO.itemSoulVessel).callIsBlackListed(entityID)) return false;
        return crazypants.enderio.config.Config.soulVesselCapturesBosses || !(entity instanceof IBossDisplayData);
    }

    public static boolean canEntityBeCapturedWithSoulVial(Entity entity) {
        return canEntityBeCapturedWithSoulVial(entity, EntityList.getEntityString(entity));
    }
}
