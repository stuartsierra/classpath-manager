package com.stuartsierra.cm;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClassLoaderCache {
    private Map<URL, CachedClassLoader> cache;

    public ClassLoaderCache() {
	cache = (Map<URL, CachedClassLoader>)
	    Collections.synchronizedMap(new HashMap<URL, CachedClassLoader>());
    }

    public ClassLoader getClassLoader(URL url) {
	CachedClassLoader ccl = cache.get(url);
	if (ccl == null || ccl.isExpired()) {
	    ccl = new CachedClassLoader(url);
	    cache.put(url, ccl);
	}
	return ccl.getClassLoader();
    }
}