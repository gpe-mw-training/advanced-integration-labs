package org.jboss.fuse.security.encryption;

import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SimpleNameSpaceContext implements NamespaceContext {

    private final Map<String, String> namespaces = new HashMap<String, String>();

    public SimpleNameSpaceContext(final Map<String, String> prefMap) {
        namespaces.putAll(prefMap);
    }

    public String getNamespaceURI(String prefix) {
        return namespaces.get(prefix);
    }

    public String getPrefix(String namespaceURI) {
        for (Map.Entry<String, String> e : namespaces.entrySet()) {
            if (e.getValue().equals(namespaceURI)) {
                return e.getKey();
            }
        }
        return null;
    }

    public Iterator getPrefixes(String uri) {
        throw new UnsupportedOperationException();
    }

}
