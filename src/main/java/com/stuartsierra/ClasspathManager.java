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

public class ClasspathManager {
    private static final String CLASSPATH_FILE_NAME = "classpath";

    private static List<URL> readClasspath(File classpathFile)
	throws IOException {

	List<URL> urls = new ArrayList<URL>();
	BufferedReader reader = new BufferedReader(new FileReader(classpathFile));
	try {
	    while(true) {
		String line = reader.readLine();
		if (line == null) break;
		StringBuilder sb = new StringBuilder();
		sb.append("file:");
		if (!line.startsWith("/")) {
		    sb.append(classpathFile.getParentFile().getAbsolutePath());
		    sb.append("/");
		}
		sb.append(line);
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

	String mainClassName = args[0];
	
	File classpathFile = new File(working_dir, CLASSPATH_FILE_NAME);

	List<URL> urls = null;
	try {
	    urls = readClasspath(classpathFile);
	} catch (IOException e) {
	    System.out.println(e.toString());
	    System.exit(-1);
	}

	URL[] urlArray = urls.toArray(new URL[0]);
	ClassLoader classloader = URLClassLoader.newInstance(urlArray);
	Thread.currentThread().setContextClassLoader(classloader);
	try {
	    Class mainClass = classloader.loadClass(mainClassName);
	    Class[] mainSignature = new Class[] { String[].class };
	    Method mainMethod = mainClass.getMethod("main", mainSignature);
	    String[] mainArgs = Arrays.copyOfRange(args, 1, args.length);
	    mainMethod.invoke(null, (Object)mainArgs);
	} catch (Exception e) {
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
