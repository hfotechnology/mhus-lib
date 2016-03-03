package de.mhus.lib.core.lang;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;

/**
 * <p>DelegateClassLoader class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class DelegateClassLoader extends ClassLoader {

	private LinkedList<Package> list = new LinkedList<Package>();
	
	
	/**
	 * <p>register.</p>
	 *
	 * @param loader a {@link java.lang.ClassLoader} object.
	 */
	public void register(ClassLoader loader) {
		synchronized(list) {
			list.add(new Package(loader));
		}
	}

	/**
	 * <p>clear.</p>
	 */
	public void clear() {
		synchronized(list) {
			list.clear();
		}
	}
	
	/** {@inheritDoc} */
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized(list) {
			for (Package p : list) {
				Class<?> clazz = p.loadClass(name, resolve);
				if (clazz != null) return clazz;
			}
		}
		return super.loadClass(name, resolve);
	}

	
	private class Package {

		private ClassLoader loader;

		public Package(ClassLoader loader) {
			this.loader = loader;
		}

		public Class<?> loadClass(String name, boolean resolve) {
			try {
				return loader.loadClass(name);
			} catch (ClassNotFoundException e) {
			}
			return null;
		}
		
		public URL getResource(String name) {
			return loader.getResource(name);
		}

		public InputStream getResourceAsStream(String name) {
			return loader.getResourceAsStream(name);
		}

		public Enumeration<URL> getResources(String name) {
			try {
				return loader.getResources(name);
			} catch (IOException e) {
			}
			return null;
		}

	}


	/** {@inheritDoc} */
	@Override
	public URL getResource(String name) {
		synchronized(list) {
			for (Package p : list) {
				URL out = p.getResource(name);
				if (out != null) return out;
			}
		}
		return super.getResource(name);
	}

	/** {@inheritDoc} */
	@Override
	public InputStream getResourceAsStream(String name) {
		synchronized(list) {
			for (Package p : list) {
				InputStream out = p.getResourceAsStream(name);
				if (out != null) return out;
			}
		}
		return super.getResourceAsStream(name);
	}

	/** {@inheritDoc} */
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		synchronized(list) {
			for (Package p : list) {
				Enumeration<URL> out = p.getResources(name);
				if (out != null) return out;
			}
		}
		return super.getResources(name);
	}
	
}
