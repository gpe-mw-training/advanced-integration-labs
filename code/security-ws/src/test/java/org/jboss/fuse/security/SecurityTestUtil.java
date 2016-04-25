package org.jboss.fuse.security;

import java.io.File;

/**
 * A utility class for security tests
 */
public final class SecurityTestUtil {

    private SecurityTestUtil() {
        // complete
    }

    public static void cleanup() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (tmpDir != null) {
            File[] tmpFiles = new File(tmpDir).listFiles();
            if (tmpFiles != null) {
                for (File tmpFile : tmpFiles) {
                    if (tmpFile.exists() && (tmpFile.getName().startsWith("ws-security.nonce.cache")
                            || tmpFile.getName().startsWith("wss4j-nonce-cache") || tmpFile.getName().startsWith("ws-security.timestamp.cache")
                            || tmpFile.getName().startsWith("wss4j-timestamp-cache"))) {
                        tmpFile.delete();
                    }
                }
            }
        }
    }
}