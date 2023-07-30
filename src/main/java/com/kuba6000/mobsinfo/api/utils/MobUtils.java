/*
 * spotless:off
 * MobsInfo - Minecraft addon
 * Copyright (C) 2023  kuba6000
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

package com.kuba6000.mobsinfo.api.utils;

import java.nio.FloatBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import com.kuba6000.mobsinfo.mixin.minecraft.RendererLivingEntityAccessor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MobUtils {

    @Deprecated
    @SideOnly(Side.CLIENT)
    public static float getDesiredScale(EntityLiving e, float desiredHeight) {
        return getDesiredScale(getMobHeight(e), desiredHeight);
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public static float getDesiredScale(float entityHeight, float desiredHeight) {
        return desiredHeight / entityHeight;
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public static float getMobHeight(EntityLiving e) {
        try {
            float eheight = e.height;
            float ewidth = e.width;
            Render r = RenderManager.instance.getEntityRenderObject(e);
            if (r instanceof RendererLivingEntity) {
                ModelBase mainModel = ((RendererLivingEntityAccessor) r).getMainModel();
                for (Object box : mainModel.boxList) {
                    if (box instanceof ModelRenderer) {
                        float minY = 999f;
                        float minX = 999f;
                        float maxY = -999f;
                        float maxX = -999f;
                        for (Object cube : ((ModelRenderer) box).cubeList) {
                            if (cube instanceof ModelBox) {
                                if (minY > ((ModelBox) cube).posY1) minY = ((ModelBox) cube).posY1;
                                if (minX > ((ModelBox) cube).posX1) minX = ((ModelBox) cube).posX1;
                                if (maxY < ((ModelBox) cube).posY2) maxY = ((ModelBox) cube).posY2;
                                if (maxX < ((ModelBox) cube).posX2) maxX = ((ModelBox) cube).posX2;
                            }
                        }
                        float cubeheight = (maxY - minY) / 10f;
                        float cubewidth = (maxX - minX) / 10f;
                        if (eheight < cubeheight) eheight = cubeheight;
                        if (ewidth < cubewidth) ewidth = cubewidth;
                    }
                }
            }
            return eheight;
        } catch (Exception ex) {
            return 1f;
        }
    }

    private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(16384);
    // private static final HashMap<String, Rectangle> sizeCache = new HashMap<>();

    @SideOnly(Side.CLIENT)
    public static Rectangle getMobSizeInGui(EntityLiving mob, int mobx, int moby, int scaled) {

        Minecraft mc = Minecraft.getMinecraft();

        ScaledResolution scale = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        /*
         * String mobSizeKey = EntityList.getEntityString(
         * mob) + mobx + "_" + moby + "_" + scaled + "_" + mc.displayHeight + "_" + scale.getScaleFactor();
         * Rectangle size = sizeCache.get(mobSizeKey);
         * if (size != null) return new Rectangle(size);
         */
        int stackdepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glPushMatrix();

        float healthScale = BossStatus.healthScale;
        int statusBarTime = BossStatus.statusBarTime;
        String bossName = BossStatus.bossName;
        boolean hasColorModifier = BossStatus.hasColorModifier;

        BossStatus.statusBarTime = 0;

        buffer.clear();

        GL11.glFeedbackBuffer(GL11.GL_2D, buffer);
        GL11.glRenderMode(GL11.GL_FEEDBACK);

        try {
            GuiInventory.func_147046_a(mobx, moby, scaled, 0.f, 0.f, mob);
        } catch (Throwable ex) {
            Tessellator tes = Tessellator.instance;
            try {
                tes.draw();
            } catch (Exception ignored) {}
        }

        int entries = GL11.glRenderMode(GL11.GL_RENDER);

        float minx = Float.MAX_VALUE;
        float maxx = Float.MIN_VALUE;
        float miny = Float.MAX_VALUE;
        float maxy = Float.MIN_VALUE;

        if (entries > 0) while (buffer.position() < entries) {
            switch ((int) buffer.get()) {
                case GL11.GL_POINT_TOKEN:
                case GL11.GL_BITMAP_TOKEN:
                case GL11.GL_DRAW_PIXEL_TOKEN:
                case GL11.GL_COPY_PIXEL_TOKEN: {
                    float[] pos = new float[2];
                    buffer.get(pos);
                    float pos_x = pos[0];
                    if (pos_x < minx) minx = pos_x;
                    if (pos_x > maxx) maxx = pos_x;
                    float pos_y = pos[1];
                    if (pos_y < miny) miny = pos_y;
                    if (pos_y > maxy) maxy = pos_y;
                    break;
                }
                case GL11.GL_LINE_TOKEN:
                case GL11.GL_LINE_RESET_TOKEN: {
                    float[] pos = new float[4];
                    buffer.get(pos);
                    for (int i = 0; i < pos.length; i += 2) {
                        float pos_x = pos[i];
                        if (pos_x < minx) minx = pos_x;
                        if (pos_x > maxx) maxx = pos_x;
                        float pos_y = pos[i + 1];
                        if (pos_y < miny) miny = pos_y;
                        if (pos_y > maxy) maxy = pos_y;
                    }
                    break;
                }
                case GL11.GL_POLYGON_TOKEN: {
                    int len = (int) buffer.get();
                    float[] pos = new float[len * 2];
                    buffer.get(pos);
                    for (int i = 0; i < pos.length; i += 2) {
                        float pos_x = pos[i];
                        if (pos_x < minx) minx = pos_x;
                        if (pos_x > maxx) maxx = pos_x;
                        float pos_y = pos[i + 1];
                        if (pos_y < miny) miny = pos_y;
                        if (pos_y > maxy) maxy = pos_y;
                    }
                    break;
                }
                case GL11.GL_PASS_THROUGH_TOKEN: {
                    buffer.get();
                    break;
                }
            }
        }

        float height_in_pixels = maxy - miny;
        float height_in_game = height_in_pixels / scale.getScaleFactor();

        float width_in_pixels = maxx - minx;
        float width_in_game = width_in_pixels / scale.getScaleFactor();

        BossStatus.healthScale = healthScale;
        BossStatus.statusBarTime = statusBarTime;
        BossStatus.bossName = bossName;
        BossStatus.hasColorModifier = hasColorModifier;

        GL11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);
        stackdepth -= GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
        if (stackdepth < 0) for (; stackdepth < 0; stackdepth++) GL11.glPopMatrix();
        if (stackdepth > 0) for (; stackdepth > 0; stackdepth--) GL11.glPushMatrix();

        GL11.glPopAttrib();

        int err;
        while ((err = GL11.glGetError()) != GL11.GL_NO_ERROR);

        return new Rectangle(
            (int) (minx / scale.getScaleFactor()),
            (int) ((mc.displayHeight - maxy) / scale.getScaleFactor()),
            (int) width_in_game,
            (int) height_in_game);
        // sizeCache.put(mobSizeKey, size);

        // return new Rectangle(size);
    }
}
