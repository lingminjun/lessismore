package com.lessismore.xauto.wirter;


import freemarker.cache.*;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class FreemarkerTemplateResolver implements TemplateResolver {

	/**
	 * FreeMarker configuration. As per the documentation, thread-safe if not
	 * altered after original initialization
	 */
	private static final Configuration CONFIGURATION;
	static {
		try {
			Logger.selectLoggerLibrary( Logger.LIBRARY_NONE );
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException( e );
		}

		CONFIGURATION = new Configuration( Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS );
		CONFIGURATION.setTemplateLoader( new SimpleClasspathLoader() );
//		CONFIGURATION.setObjectWrapper( new DefaultObjectWrapper( Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS ) );
//		CONFIGURATION.setSharedVariable(
//				"includeModel",
//				new ModelIncludeDirective( CONFIGURATION )
//		);
		// do not refresh/gc the cached templates, as we never change them at runtime
		CONFIGURATION.setCacheStorage( new StrongCacheStorage() );
		CONFIGURATION.setTemplateUpdateDelay( Integer.MAX_VALUE );
		CONFIGURATION.setLocalizedLookup( false );
	}
	private static final Configuration STRING_CONFIGURATION;
	static {
		STRING_CONFIGURATION = new Configuration( Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS );
		STRING_CONFIGURATION.setTemplateLoader( new StringTemplateLoader() );
		STRING_CONFIGURATION.setDefaultEncoding("UTF-8");
		STRING_CONFIGURATION.setCacheStorage(new NoCacheStorage());
	}

	private static final class SimpleClasspathLoader implements TemplateLoader {
		@Override
		public Reader getReader(Object name, String encoding) throws IOException {
			URL url = getClass().getClassLoader().getResource( String.valueOf( name ) );
			if ( url == null ) {
				throw new IllegalStateException( name + " not found on classpath" );
			}
			URLConnection connection = url.openConnection();

			// don't use jar-file caching, as it caused occasionally closed input streams [at least under JDK 1.8.0_25]
			connection.setUseCaches( false );

			InputStream is = connection.getInputStream();

			return new InputStreamReader( is, StandardCharsets.UTF_8 );
		}

		@Override
		public long getLastModified(Object templateSource) {
			return 0;
		}

		@Override
		public Object findTemplateSource(String name) throws IOException {
			return name;
		}

		@Override
		public void closeTemplateSource(Object templateSource) throws IOException {
		}
	}

	private static final class NoCacheStorage implements ConcurrentCacheStorage, CacheStorageWithGetSize {

		public NoCacheStorage() {
		}

		public boolean isConcurrent() {
			return true;
		}

		public Object get(Object key) {
			return null;
		}

		public void put(Object key, Object value) {
		}

		public void remove(Object key) {
		}

		public int getSize() {
			return 0;
		}

		public void clear() {

		}
	}

	@Override
	public String resolver(String relativePath, String suffix, Object model) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			Template temp = CONFIGURATION.getTemplate(relativePath + "." + suffix, "utf-8");
			temp.process(model, new OutputStreamWriter(bos));
			String result = bos.toString("UTF-8");
//			System.out.println(result);
			return result;
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {}
		}
		return null;
	}

	/**
	 * 字符模板渲染
	 * @param template
	 * @param model
	 * @return
	 */
	public String resolverStringTemplate(String template, Object model) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			Template temp = new Template("" + System.currentTimeMillis(), template, STRING_CONFIGURATION);
			temp.process(model, new OutputStreamWriter(bos));
			String result = bos.toString("UTF-8");
			return result;
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {}
		}
		return null;
	}
}
