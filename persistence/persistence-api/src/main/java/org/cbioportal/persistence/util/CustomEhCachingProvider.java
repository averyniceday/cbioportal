/*
 * Copyright (c) 2019 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cbioportal.persistence.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import javax.cache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.Configuration;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.xml.XmlConfiguration;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class CustomEhCachingProvider extends EhcacheCachingProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CustomEhCachingProvider.class);

    @Value("${ehcache.xml_configuration:/ehcache.xml}")
    private String xmlConfigurationFile;

    @Value("${ehcache.cache_enabled:false}")
    private Boolean cacheEnabled;

    @Value("${ehcache.statistics_enabled:false}")
    private Boolean statisticsEnabled;

    @Value("${ehcache.general_repository_cache.max_mega_bytes_heap:1024}")
    private Integer generalRepositoryCacheMaxMegaBytes;

    @Value("${ehcache.static_repository_cache_one.max_mega_bytes_heap:30}")
    private Integer staticRepositoryCacheOneMaxMegaBytes;

    @Value("${ehcache.persistence_path:/tmp}")
    private String persistencePath;

    @Value("${ehcache.general_repository_cache.max_mega_bytes_local_disk:4096}")
    private Integer generalRepositoryCacheMaxMegaBytesLocalDisk;

    @Value("${ehcache.static_repository_cache_one.max_mega_bytes_local_disk:32}")
    private Integer staticRepositoryCacheOneMaxMegaBytesLocalDisk;

    @Override
    public CacheManager getCacheManager() {

        CacheManager toReturn = null;
         try {
            if (cacheEnabled != null && cacheEnabled) {
                LOG.info("Caching is enabled, using '" + xmlConfigurationFile + "' for configuration");

/*
    1. We cannot create a cache manager from an XML file that contains ${property.name}. So we cannot create a cache manager and then modify
 it or use it as a template.
    2. We cannot have an XML file with no ${property.name} that we load with defaults and then modify the defaults.  I don't think we have access
to things like the cache size, and the directory would likely be created right away.
*/

                XmlConfiguration xmlConfiguration = new XmlConfiguration(getClass().getResource(xmlConfigurationFile)); 
                LOG.info("XmlConfiguration: " + xmlConfiguration);
                // TODO make StaticRepositoryCacheOne and GeneralRepositoryCache strings variables
                LOG.info("xmlConfiguration.newCacheConfigurationBuilderFromTemplate(\"GeneralRepositoryCache\", Object.class, Object.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(2, MemoryUnit.B)): " + xmlConfiguration.newCacheConfigurationBuilderFromTemplate("GeneralRepositoryCache", Object.class, Object.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(2, MemoryUnit.B)));
                CacheConfiguration<Object, Object> generalRepositoryCacheConfiguration = xmlConfiguration.newCacheConfigurationBuilderFromTemplate("GeneralRepositoryCache", Object.class, Object.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(2, MemoryUnit.B)).build();
                CacheConfiguration<Object, Object> staticRepositoryCacheOneConfiguration = xmlConfiguration.newCacheConfigurationBuilderFromTemplate("StaticRepositoryCacheOne", Object.class, Object.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(2, MemoryUnit.B)).build(); 

                // TODO can we also modify the persistance path? - See: Property replacement in XML configuration files in http://www.ehcache.org/documentation/3.6/xml.html
                // I don't think the properties will be replaced with the java system properties

                Map<String, CacheConfiguration<?, ?>> caches = new HashMap<>();
                caches.put("GeneralRepositoryCache", generalRepositoryCacheConfiguration);
                caches.put("StaticRepositoryCacheOne", staticRepositoryCacheOneConfiguration);

                Configuration configuration = new DefaultConfiguration(caches, this.getDefaultClassLoader());

                toReturn = this.getCacheManager(this.getDefaultURI(), configuration);

                toReturn.enableStatistics("GeneralRepositoryCache", statisticsEnabled);
                toReturn.enableStatistics("StaticRepositoryCacheOne", statisticsEnabled);
            } else {
                LOG.info("Caching is disabled");
                // we can not really disable caching,
                // we can not make a cache of 0 objects, 
                // and we can not make a heap of memory size 0, so make a tiny heap
                CacheConfiguration<Object, Object> generalRepositoryCacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class,
                    Object.class,
                    ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1, MemoryUnit.B)).build();
                CacheConfiguration<Object, Object> staticRepositoryCacheOneConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class,
                    Object.class,
                    ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1, MemoryUnit.B)).build();
                
                Map<String, CacheConfiguration<?, ?>> caches = new HashMap<>();
                caches.put("GeneralRepositoryCache", generalRepositoryCacheConfiguration);
                caches.put("StaticRepositoryCacheOne", staticRepositoryCacheOneConfiguration);

                Configuration configuration = new DefaultConfiguration(caches, this.getDefaultClassLoader());

                toReturn = this.getCacheManager(this.getDefaultURI(), configuration);
            }
        }
        catch (Exception e) {
            LOG.error(e.getClass().getName() + ": " + e.getMessage());
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOG.error(stackTrace.toString());
        }
        return toReturn;
    }
}
 
