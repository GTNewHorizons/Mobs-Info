package com.kuba6000.mobsinfo.loader.extras;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Assume;
import org.junit.Test;

public class ReliquaryCompatibilityTest {

    @Test
    public void selectsCompatibleDropSupportForReleasedJars() throws Exception {
        String jars = System.getProperty("reliquaryTestJars");
        Assume.assumeNotNull(jars);
        String[] paths = jars.split(";");
        assertEquals("Provide stable 1.2, beta 1.2.257 and 1.2.1.483 in that order", 3, paths.length);
        String[] versions = { "1.2", "1.2", "1.7.10-1.2.1.483" };
        ReliquaryCompatibility[] expected = { ReliquaryCompatibility.LEGACY, ReliquaryCompatibility.MODERN,
            ReliquaryCompatibility.MODERN };
        for (int i = 0; i < paths.length; i++) {
            try (URLClassLoader loader = new URLClassLoader(
                new URL[] { new File(paths[i]).toURI()
                    .toURL() },
                getClass().getClassLoader()) {

                @Override
                protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    // Each real release supplies its own Reliquary classes, not the compile dependency.
                    if (name.startsWith("xreliquary.")) {
                        synchronized (getClassLoadingLock(name)) {
                            Class<?> loaded = findLoadedClass(name);
                            if (loaded == null) loaded = findClass(name);
                            if (resolve) resolveClass(loaded);
                            return loaded;
                        }
                    }
                    return super.loadClass(name, resolve);
                }
            }) {
                assertEquals(paths[i], expected[i], ReliquaryCompatibility.detect(versions[i], loader));
            }
        }
    }
}
