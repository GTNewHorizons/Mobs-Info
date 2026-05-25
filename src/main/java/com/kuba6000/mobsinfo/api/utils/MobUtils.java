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
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import com.kuba6000.mobsinfo.mixin.early.minecraft.EntityAccessor;
import com.kuba6000.mobsinfo.mixin.early.minecraft.RendererLivingEntityAccessor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MobUtils {

    private static final Logger LOG = LogManager.getLogger("mobsinfo[Mob Render]");

    private static final int PREVIEW_BOX_X = 7;
    private static final int PREVIEW_BOX_Y = 8;
    private static final int PREVIEW_BOX_WIDTH = 48;
    private static final int PREVIEW_BOX_HEIGHT = 52;
    private static final int PREVIEW_CONTENT_PADDING = 2;
    private static final int PREVIEW_CONTENT_WIDTH = PREVIEW_BOX_WIDTH - (PREVIEW_CONTENT_PADDING * 2);
    private static final int PREVIEW_CONTENT_HEIGHT = PREVIEW_BOX_HEIGHT - (PREVIEW_CONTENT_PADDING * 2);
    private static final int PREVIEW_ANCHOR_X = 31;
    private static final int PREVIEW_ANCHOR_Y = 50;
    private static final float PREVIEW_MEASURE_SCALE = 20f;
    private static final int PREVIEW_MAX_SCALE = 36;
    private static final float PREVIEW_ZOOM_MIN = 0.5f;
    private static final float PREVIEW_ZOOM_MAX = 2.5f;
    private static final float PREVIEW_ZOOM_STEP = 0.1f;
    private static float previewZoom = 1f;

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

    private static class PreviewRenderLayout {

        final int anchorX;
        final int anchorY;
        final int scale;

        PreviewRenderLayout(int anchorX, int anchorY, int scale) {
            this.anchorX = anchorX;
            this.anchorY = anchorY;
            this.scale = scale;
        }
    }

    private static class PreviewReferencePoint {

        final int anchorX;
        final int anchorY;

        PreviewReferencePoint(int anchorX, int anchorY) {
            this.anchorX = anchorX;
            this.anchorY = anchorY;
        }
    }

    private static class PreviewBounds {

        final float width;
        final float height;

        PreviewBounds(float width, float height) {
            this.width = Math.max(0.1f, width);
            this.height = Math.max(0.1f, height);
        }
    }

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
            LOG.warn("Mob preview feedback buffer overflow for {}", getMobDebugName(mob));
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
        renderMobPreview(mob, guiLeft, guiTop, mouseX, mouseY, PREVIEW_ANCHOR_X, PREVIEW_ANCHOR_Y);
    }

    @SideOnly(Side.CLIENT)
    public static void renderMobPreview(EntityLiving mob, float guiLeft, float guiTop, int mouseX, int mouseY,
        int anchorX, int anchorY) {
        String stage = "origin";
        World originalWorld = null;
        boolean swappedWorld = false;
        PreviewReferencePoint referencePoint = new PreviewReferencePoint(anchorX, anchorY);
        try {
            stage = "swap_world";
            Minecraft mc = Minecraft.getMinecraft();
            EntityAccessor entityAccessor = (EntityAccessor) mob;
            originalWorld = entityAccessor.getWorldObj();
            if (mc.theWorld != null && originalWorld != mc.theWorld) {
                entityAccessor.setWorldObj(mc.theWorld);
                swappedWorld = true;
            }
            int previewGuiLeft = Math.round(guiLeft);
            int previewGuiTop = Math.round(guiTop);
            stage = "layout";
            PreviewRenderLayout layout = getPreviewRenderLayout(mob);
            stage = "scissor";
            applyPreviewScissor(previewGuiLeft, previewGuiTop);
            stage = "primary_draw";
            GuiInventory.func_147046_a(
                layout.anchorX,
                layout.anchorY,
                layout.scale,
                (guiLeft + referencePoint.anchorX) - mouseX,
                guiTop + referencePoint.anchorY - 25 - mouseY,
                mob);
        } catch (Throwable ex) {
            LOG.error(
                "Mob preview primary render failed at stage {} for {}: {}",
                stage,
                getMobDebugName(mob),
                ex.toString(),
                ex);
            try {
                renderMobPreviewFallback(mob, guiLeft, guiTop, mouseX, mouseY, referencePoint);
            } catch (Throwable fallbackEx) {
                LOG.error(
                    "Mob preview fallback render failed for {}: {}",
                    getMobDebugName(mob),
                    fallbackEx.toString(),
                    fallbackEx);
            }
        } finally {
            if (swappedWorld) ((EntityAccessor) mob).setWorldObj(originalWorld);
        }
    }

    @SideOnly(Side.CLIENT)
    private static PreviewRenderLayout getPreviewRenderLayout(EntityLiving mob) {
        PreviewBounds bounds = getPreviewBounds(mob);
        float fitHeightScale = PREVIEW_CONTENT_HEIGHT / bounds.height;
        float fitWidthScale = PREVIEW_CONTENT_WIDTH / bounds.width;

        int renderScale = Math.max(1, Math.round(Math.min(fitHeightScale, fitWidthScale) * previewZoom));
        renderScale = Math.min(renderScale, PREVIEW_MAX_SCALE);

        float targetCenterX = PREVIEW_BOX_X + (PREVIEW_BOX_WIDTH / 2f);
        float targetCenterY = PREVIEW_BOX_Y + (PREVIEW_BOX_HEIGHT / 2f);
        float anchorX = targetCenterX;
        float anchorY = targetCenterY + ((bounds.height * renderScale) / 2f);

        return new PreviewRenderLayout(Math.round(anchorX), Math.round(anchorY), renderScale);
    }

    private static PreviewBounds getPreviewBounds(EntityLiving mob) {
        float width = Math.max(0.1f, mob.width);
        float height = Math.max(0.1f, mob.height);

        PreviewBounds modelBounds = getModelPreviewBounds(mob);
        if (modelBounds != null) {
            width = Math.max(width, modelBounds.width);
            height = Math.max(height, modelBounds.height);
        }

        return new PreviewBounds(width, height);
    }

    private static PreviewBounds getModelPreviewBounds(EntityLiving mob) {
        try {
            Render render = RenderManager.instance.getEntityRenderObject(mob);
            if (!(render instanceof RendererLivingEntity)) return null;

            ModelBase mainModel = ((RendererLivingEntityAccessor) render).getMainModel();
            ModelBoundsAccumulator bounds = new ModelBoundsAccumulator();
            for (Object box : mainModel.boxList) {
                if (box instanceof ModelRenderer)
                    accumulateModelRendererBounds(bounds, (ModelRenderer) box, 0f, 0f, 0f);
            }

            if (!bounds.hasBounds) return null;
            return new PreviewBounds(
                Math.max(bounds.maxX - bounds.minX, bounds.maxZ - bounds.minZ),
                bounds.maxY - bounds.minY);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void accumulateModelRendererBounds(ModelBoundsAccumulator bounds, ModelRenderer renderer,
        float parentX, float parentY, float parentZ) {
        if (renderer.isHidden || !renderer.showModel) return;

        float originX = parentX + renderer.rotationPointX + renderer.offsetX;
        float originY = parentY + renderer.rotationPointY + renderer.offsetY;
        float originZ = parentZ + renderer.rotationPointZ + renderer.offsetZ;

        for (Object cube : renderer.cubeList) {
            if (cube instanceof ModelBox) {
                ModelBox box = (ModelBox) cube;
                bounds.include((originX + box.posX1) / 16f, (originY + box.posY1) / 16f, (originZ + box.posZ1) / 16f);
                bounds.include((originX + box.posX2) / 16f, (originY + box.posY2) / 16f, (originZ + box.posZ2) / 16f);
            }
        }

        if (renderer.childModels == null) return;
        for (Object child : renderer.childModels) {
            if (child instanceof ModelRenderer) {
                accumulateModelRendererBounds(bounds, (ModelRenderer) child, originX, originY, originZ);
            }
        }
    }

    private static class ModelBoundsAccumulator {

        boolean hasBounds = false;
        float minX;
        float minY;
        float minZ;
        float maxX;
        float maxY;
        float maxZ;

        void include(float x, float y, float z) {
            if (!hasBounds) {
                minX = maxX = x;
                minY = maxY = y;
                minZ = maxZ = z;
                hasBounds = true;
                return;
            }
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }
    }

    public static boolean isPreviewBoxHovered(float guiLeft, float guiTop, int mouseX, int mouseY) {
        float left = guiLeft + PREVIEW_BOX_X;
        float top = guiTop + PREVIEW_BOX_Y;
        float right = left + PREVIEW_BOX_WIDTH;
        float bottom = top + PREVIEW_BOX_HEIGHT;
        return mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom;
    }

    public static void adjustPreviewZoom(int scroll) {
        float zoomDelta = scroll > 0 ? PREVIEW_ZOOM_STEP : -PREVIEW_ZOOM_STEP;
        previewZoom += zoomDelta;
        if (previewZoom < PREVIEW_ZOOM_MIN) previewZoom = PREVIEW_ZOOM_MIN;
        if (previewZoom > PREVIEW_ZOOM_MAX) previewZoom = PREVIEW_ZOOM_MAX;
    }

    @SideOnly(Side.CLIENT)
    private static void applyPreviewScissor(int guiLeft, int guiTop) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scale = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int scaleFactor = scale.getScaleFactor();

        int scissorX = (guiLeft + PREVIEW_BOX_X) * scaleFactor;
        int scissorY = mc.displayHeight - ((guiTop + PREVIEW_BOX_Y + PREVIEW_BOX_HEIGHT) * scaleFactor);
        int scissorWidth = PREVIEW_BOX_WIDTH * scaleFactor;
        int scissorHeight = PREVIEW_BOX_HEIGHT * scaleFactor;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    @SideOnly(Side.CLIENT)
    private static void renderMobPreviewFallback(EntityLiving mob, float guiLeft, float guiTop, int mouseX, int mouseY,
        PreviewReferencePoint referencePoint) {
        int mobx = referencePoint.anchorX;
        int moby = referencePoint.anchorY;
        Rectangle measuredBounds = getMobSizeInGui(mob, mobx, moby, (int) PREVIEW_MEASURE_SCALE);

        float newScale = PREVIEW_CONTENT_HEIGHT / Math.max(1f, measuredBounds.getHeight());
        float newScaleX = PREVIEW_CONTENT_WIDTH / Math.max(1f, measuredBounds.getWidth());
        if (newScaleX < newScale) newScale = newScaleX;

        newScale = (float) Math.round(PREVIEW_MEASURE_SCALE * newScale * previewZoom) / PREVIEW_MEASURE_SCALE;

        float measuredCenterX = measuredBounds.getX() + (measuredBounds.getWidth() / 2f);
        float measuredCenterY = measuredBounds.getY() + (measuredBounds.getHeight() / 2f);
        float targetCenterX = guiLeft + PREVIEW_BOX_X + (PREVIEW_BOX_WIDTH / 2f);
        float targetCenterY = guiTop + PREVIEW_BOX_Y + (PREVIEW_BOX_HEIGHT / 2f);
        float measuredOffsetX = measuredCenterX - (guiLeft + mobx);
        float measuredOffsetY = measuredCenterY - (guiTop + moby);
        int anchorX = Math.round(targetCenterX - guiLeft - (measuredOffsetX * newScale));
        int anchorY = Math.round(targetCenterY - guiTop - (measuredOffsetY * newScale));

        GuiInventory.func_147046_a(
            anchorX,
            anchorY,
            Math.max(1, Math.round(PREVIEW_MEASURE_SCALE * newScale)),
            (guiLeft + mobx) - mouseX,
            guiTop + moby - 25 - mouseY,
            mob);
    }

    private static String getMobDebugName(EntityLiving mob) {
        String entityId = EntityList.getEntityString(mob);
        String className = mob.getClass()
            .getName();
        if (entityId != null) return entityId + " (" + className + ")";
        return className;
    }
}
