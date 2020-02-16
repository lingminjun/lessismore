package com.lessismore.xauto.wirter;


import freemarker.cache.StrongCacheStorage;
import freemarker.cache.TemplateLoader;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;


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

	/**
	 * Simplified template loader that avoids reading modification timestamps and disables the jar-file caching.
	 *
	 * @author Andreas Gudian
	 */
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
}
