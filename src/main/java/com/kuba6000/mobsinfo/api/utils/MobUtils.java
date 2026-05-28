/*
 * spotless:off
 * MobsInfo - Minecraft addon
 * Copyright (C) 2023-2025  kuba6000
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
import java.util.Map;
import java.util.WeakHashMap;

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

import com.kuba6000.mobsinfo.mixin.early.minecraft.RendererLivingEntityAccessor;

import codechicken.nei.scroll.GuiHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MobUtils {

    private static final int PREVIEW_BOX_X = 8;
    private static final int PREVIEW_BOX_Y = 8;
    private static final int PREVIEW_BOX_WIDTH = 46;
    private static final int PREVIEW_BOX_HEIGHT = 52;
    private static final int PREVIEW_ANCHOR_X = 31;
    private static final int PREVIEW_ANCHOR_Y = 50;
    private static final float PREVIEW_MEASURE_SCALE = 20f;
    private static final float PREVIEW_ZOOM_MIN = 0.75f;
    private static final float PREVIEW_ZOOM_MAX = 1.5f;
    private static final float PREVIEW_ZOOM_STEP = 0.05f;
    private static final Map<EntityLiving, Float> previewZooms = new WeakHashMap<>();

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

    // This size allows all vanilla mobs to render
    private static FloatBuffer buffer = BufferUtils.createFloatBuffer(16_384);
    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
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

        matrixBuffer.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrixBuffer);

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

        if (entries > 0) {
            while (buffer.position() < entries) {
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
        } else if (entries == -1) {
            buffer = BufferUtils.createFloatBuffer(buffer.capacity() << 2);
            System.gc();
            float x = matrixBuffer.get(12);
            float y = matrixBuffer.get(13);
            return new Rectangle((int) x, (int) y, 48, 54);
        }

        float height_in_pixels = maxy - miny;
        float height_in_game = height_in_pixels / scale.getScaleFactor();

        float width_in_pixels = maxx - minx;
        float width_in_game = width_in_pixels / scale.getScaleFactor();

        BossStatus.healthScale = healthScale;
        BossStatus.statusBarTime = statusBarTime;
        BossStatus.bossName = bossName;
        BossStatus.hasColorModifier = hasColorModifier;

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        stackdepth -= GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
        if (stackdepth < 0) for (; stackdepth < 0; stackdepth++) GL11.glPopMatrix();
        if (stackdepth > 0) {
            for (; stackdepth > 0; stackdepth--) GL11.glPushMatrix();
            GL11.glLoadMatrix(matrixBuffer);
        }
        GL11.glPopAttrib();

        // noinspection StatementWithEmptyBody
        while ((GL11.glGetError()) != GL11.GL_NO_ERROR);

        return new Rectangle(
            (int) (minx / scale.getScaleFactor()),
            (int) ((mc.displayHeight - maxy) / scale.getScaleFactor()),
            (int) width_in_game,
            (int) height_in_game);
        // sizeCache.put(mobSizeKey, size);

        // return new Rectangle(size);
    }

    @SideOnly(Side.CLIENT)
    public static void renderMobPreview(EntityLiving mob, float guiLeft, float guiTop, int mouseX, int mouseY) {
        int mobx = PREVIEW_ANCHOR_X;
        int moby = PREVIEW_ANCHOR_Y;

        // GL feedback bounds must not be clipped by any active scissor state.
        boolean scissorWasEnabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        if (scissorWasEnabled) GL11.glDisable(GL11.GL_SCISSOR_TEST);
        Rectangle bounds;
        try {
            bounds = getMobSizeInGui(mob, mobx, moby, (int) PREVIEW_MEASURE_SCALE);
        } finally {
            if (scissorWasEnabled) GL11.glEnable(GL11.GL_SCISSOR_TEST);
        }

        float ylocal = (bounds.getY() + bounds.getHeight()) - guiTop;
        float wantedy = 54.f;

        float newScale = 40.f / bounds.getHeight();
        float newScaleX = 38.f / bounds.getWidth();
        if (newScaleX < newScale) newScale = newScaleX;

        newScale = (float) Math.round(PREVIEW_MEASURE_SCALE * newScale * getPreviewZoom(mob)) / PREVIEW_MEASURE_SCALE;

        float a = moby - ylocal;
        float aa = a - (a * newScale);
        float aaa = (wantedy - ylocal) - aa;

        float finalNewScale = newScale;
        GuiHelper.useScissor(PREVIEW_BOX_X, PREVIEW_BOX_Y, PREVIEW_BOX_WIDTH, PREVIEW_BOX_HEIGHT, () -> {
            GuiInventory.func_147046_a(
                mobx,
                (int) (moby + aaa),
                Math.round(PREVIEW_MEASURE_SCALE * finalNewScale),
                (guiLeft + mobx) - mouseX,
                guiTop + moby - 25 - mouseY,
                mob);
        });
    }

    public static boolean isPreviewBoxHovered(float guiLeft, float guiTop, int mouseX, int mouseY) {
        float left = guiLeft + PREVIEW_BOX_X;
        float top = guiTop + PREVIEW_BOX_Y;
        float right = left + PREVIEW_BOX_WIDTH;
        float bottom = top + PREVIEW_BOX_HEIGHT;
        return mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom;
    }

    public static void adjustPreviewZoom(EntityLiving mob, int scroll) {
        float zoomDelta = scroll > 0 ? PREVIEW_ZOOM_STEP : -PREVIEW_ZOOM_STEP;
        setPreviewZoom(mob, getPreviewZoom(mob) + zoomDelta);
    }

    private static float getPreviewZoom(EntityLiving mob) {
        Float zoom = previewZooms.get(mob);
        return zoom == null ? 1f : zoom;
    }

    private static void setPreviewZoom(EntityLiving mob, float zoom) {
        if (zoom < PREVIEW_ZOOM_MIN) zoom = PREVIEW_ZOOM_MIN;
        if (zoom > PREVIEW_ZOOM_MAX) zoom = PREVIEW_ZOOM_MAX;
        previewZooms.put(mob, zoom);
    }
}
