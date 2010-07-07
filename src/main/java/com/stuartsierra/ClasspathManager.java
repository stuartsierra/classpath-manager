package com.stuartsierra;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.martiansoftware.nailgun.NGContext;

import com.stuartsierra.cm.CompositeClassLoader;
import com.stuartsierra.cm.ClassLoaderCache;

public class ClasspathManager {
    private static final String CLASSPATH_FILE_NAME = "classpath";

    private static final Class[] MAIN_METHOD_SIGNATURE = new Class[] { String[].class };

    private static final ClassLoaderCache cache;

    static {
	cache = new ClassLoaderCache();
    }

    private static List<URL> readClasspath(File classpathFile)
	throws IOException {

	List<URL> urls = new ArrayList<URL>();
	BufferedReader reader = new BufferedReader(new FileReader(classpathFile));
	try {
	    while(true) {
		String line = reader.readLine();
		if (line == null) break;
		line = line.trim();
		if (line.isEmpty()) break;

		StringBuilder sb = new StringBuilder();
		if (!line.startsWith("/")) {
		    sb.append(classpathFile.getParentFile().getAbsolutePath());
		    sb.append("/");
		}
		sb.append(line);

		File file = new File(sb.toString());
		sb = new StringBuilder("file:");
		sb.append(file.getAbsolutePath());
		if (file.isDirectory()) {
		    sb.append("/");
		}

		try {
		    URL url = new URL(sb.toString());
		    urls.add(url);
		} catch (MalformedURLException e) {
		    System.out.println(e.toString());
		}
	    }
	} finally {
	    reader.close();
	}
	return urls;
    }

    public static void sharedMain(String working_dir, String[] args) {
	if (args.length == 0) {
	    System.out.println("Usage: ClasspathManager <main-class>");
	    System.exit(1);
	}

	final String mainClassName = args[0];
	final String[] mainArgs = Arrays.copyOfRange(args, 1, args.length);
	
	File classpathFile = new File(working_dir, CLASSPATH_FILE_NAME);

	List<URL> urls = null;
	try {
	    urls = readClasspath(classpathFile);
	} catch (IOException e) {
	    System.out.println(e.toString());
	    System.exit(-1);
	}

	final URL[] urlArray = urls.toArray(new URL[0]);

	Thread thread = new Thread() {
		public void run() {
		    List<ClassLoader> classloaders = new ArrayList<ClassLoader>();
		    for (int i = 0; i < urlArray.length; i++) {
			classloaders.add(cache.getClassLoader(urlArray[i]));
		    }
		    ClassLoader compositeLoader =
			new CompositeClassLoader(classloaders,
						 ClasspathManager.class.getClassLoader());
		    Thread.currentThread().setContextClassLoader(compositeLoader);
		    try {
			Class mainClass = compositeLoader.loadClass(mainClassName);
			Method mainMethod = mainClass.getMethod("main",
								MAIN_METHOD_SIGNATURE);
			mainMethod.invoke(null, (Object)mainArgs);
		    } catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		    }	    
		}
	    };

	try {
	    thread.start();
	    thread.join();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
    }
    
    public static void main(String[] args) {
	sharedMain(System.getProperty("user.dir"), args);
    }

    public static void nailMain(NGContext context) {
	sharedMain(context.getWorkingDirectory(), context.getArgs());
    }
}
