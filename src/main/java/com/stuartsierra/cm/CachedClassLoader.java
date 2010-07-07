package com.stuartsierra.cm;

import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;

public class CachedClassLoader {
    private final URL url;
    private final URLClassLoader classloader;
    private final long timestamp;

    public CachedClassLoader(URL url) {
	this.url = url;
	URL[] urls = new URL[] { url };
	this.classloader = new URLClassLoader(urls);
	this.timestamp = System.currentTimeMillis();
    }

    public ClassLoader getClassLoader() {
	return this.classloader;
    }

    public boolean isExpired() {
	File file = new File(this.url.getFile());
	if (file.isFile() && file.lastModified() > timestamp) {
	    return true;
	} else {
	    return isExpiredByDirectory(file);
	}
    }

    private boolean isExpiredByDirectory(File file) {
	File[] files = file.listFiles();
	if (files == null) return false;
	for (int i = 0; i < files.length; i++) {
	    if (files[i].lastModified() > timestamp) {
		return true;
	    } else if (files[i].isDirectory() &&
		       isExpiredByDirectory(files[i])) {
		return true;
	    }
	}
	return false;
    }
}