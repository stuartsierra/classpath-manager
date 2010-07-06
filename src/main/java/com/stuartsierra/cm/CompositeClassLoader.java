package com.stuartsierra.cm;

import java.io.IOException;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

public class CompositeClassLoader extends SecureClassLoader {
    private final Collection<ClassLoader> classloaders;

    public CompositeClassLoader() {
	super();
	this.classloaders = new ArrayList<ClassLoader>(0);
    }

    public CompositeClassLoader(final Collection<ClassLoader> classloaders,
				ClassLoader parent) {
	super(parent);
	this.classloaders = new ArrayList<ClassLoader>(classloaders.size());
	this.classloaders.addAll(classloaders);
    }

    protected Class loadClass(String name, boolean resolve)
	throws ClassNotFoundException {

	Class klass = null;
	klass = loadFromClassLoader(name, this.getParent());
	if (klass != null) return klass;

	Iterator<ClassLoader> iter = classloaders.iterator();
	while (iter.hasNext()) {
	    ClassLoader loader = iter.next();
	    klass = loadFromClassLoader(name, loader);
	    if (klass != null) return klass;
	}

	throw new ClassNotFoundException("Could not find class " + name);
    }

    public URL getResource(String name) {
	URL url = null;
	url = this.getParent().getResource(name);
	if (url != null) return url;

	Iterator<ClassLoader> iter = classloaders.iterator();
	while (iter.hasNext()) {
	    ClassLoader loader = iter.next();
	    url = loader.getResource(name);
	    if (url != null) return url;
	}
	
	return null;
    }
    
    public Enumeration<URL> getResources(String name) throws IOException {
	Collection<URL> urls = new ArrayList<URL>();
	Enumeration<URL> URLenum;
	
	URLenum = this.getParent().getResources(name);
	if (URLenum != null) {
	    while (URLenum.hasMoreElements()) {
		URL url = URLenum.nextElement();
		urls.add(url);
	    }
	}

	Iterator<ClassLoader> iter = classloaders.iterator();
	while (iter.hasNext()) {
	    ClassLoader loader = iter.next();
	    URLenum = loader.getResources(name);
	    if (URLenum != null) {
		while (URLenum.hasMoreElements()) {
		    URL url = URLenum.nextElement();
		    urls.add(url);
		}
	    }
	}
	
	return Collections.enumeration(urls);
    }
    
    private static Class loadFromClassLoader(String classname, ClassLoader loader) {
	try {
	    return loader.loadClass(classname);
	} catch (ClassNotFoundException e) {
	    return null;
	}
    }
}