package com.kuba6000.mobsinfo.api;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import net.minecraft.init.Bootstrap;

import org.junit.Test;
import org.mockito.MockedStatic;

import cpw.mods.fml.common.Loader;

public class DummyWorldTileEntityTest {

    @Test
    public void tileEntityLookupsReturnNoObjectWithoutLoadingChunks() {
        try (MockedStatic<Loader> forge = mockStatic(Loader.class)) {
            forge.when(Loader::instance)
                .thenReturn(mock(Loader.class));
            Bootstrap.func_151354_b();
            DummyWorld world = new DummyWorld();
            assertNull(world.getTileEntity(0, 64, 0));
            assertNull(world.getTileEntity(-32, 0, -16));
            assertNull(world.getTileEntity(24, 64, 24));
            assertNull(world.getTileEntity(0, 256, 0));
        }
    }
}
