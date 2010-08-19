package honeycrm.server;

import honeycrm.server.transfer.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

/**
 * Caching wrapper around ReflectionHelper class. Caches expensive reflective operations when possible.
 */
public class CachingReflectionHelper extends ReflectionHelper {
	private static final Logger log = Logger.getLogger(CachingReflectionHelper.class.getName());
	private static Cache cache;
	private static final Map<Class<?>, Field[]> cacheGetAllFields = new HashMap<Class<?>, Field[]>();

	static { // setup the cache instance once
		try {
			final CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			try {
				cache = cacheFactory.createCache(new HashMap());
			} catch (RuntimeException e) {
				log.warning("RuntimeException occured while creating cache. Disabled Caching.");
				cache = null;
			}
			log.info("Cache enabled.");
		} catch (CacheException e) {
			log.warning("Cannot instantiate cache. Disabled caching.");
			cache = null;
		}
	}

	private boolean isCacheEnabled() {
		return cache != null;
	}

	@Override
	public Field[] getAllFields(Class classSrc) {
		// if (isCacheEnabled()) {
		if (!cacheGetAllFields.containsKey(classSrc)) {
			cacheGetAllFields.put(classSrc, super.getAllFields(classSrc));
		}
		return cacheGetAllFields.get(classSrc);
		// } else {
		// return super.getAllFields(classSrc);
		// }
	}
}
