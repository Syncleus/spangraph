/**
 * Copyright: (c) Syncleus, Inc.
 *
 * You may redistribute and modify this source code under the terms and
 * conditions of the Open Source Community License - Type C version 1.0
 * or any later version as published by Syncleus, Inc. at www.syncleus.com.
 * There should be a copy of the license included with this file. If a copy
 * of the license is not included you are granted no right to distribute or
 * otherwise use this file except through a legal and valid license. You
 * should also contact Syncleus, Inc. at the information below if you cannot
 * find a license:
 *
 * Syncleus, Inc.
 * 2604 South 12th Street
 * Philadelphia, PA 19148
 */
package com.syncleus.spangraph;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by me on 6/13/15.
 */
public class QueryTest {

    @Test
    public void testInfiniPeerCache() {



        GlobalConfigurationBuilder globalConfigBuilder = new GlobalConfigurationBuilder();

        globalConfigBuilder.transport().nodeName("x")
                .defaultTransport()
                .addProperty("configurationFile", "fast.xml");



        globalConfigBuilder.globalJmxStatistics().allowDuplicateDomains(true)
                .build();

        Configuration config = new ConfigurationBuilder()
                /*.persistence()
                .addSingleFileStore()
                .location("/tmp/t")
                .maxEntries(1000)*/


                .unsafe()
                .clustering()
                        //.cacheMode(CacheMode.DIST_SYNC)
                .cacheMode(CacheMode.DIST_SYNC)
                .sync()
                .l1().lifespan(25000L)
                .hash().numOwners(3).build();

        InfiniPeer cacheMan = InfiniPeer.the(
                globalConfigBuilder,
                config
        );



        final Cache<Object, Object> cache = cacheMan.the("abc", true);


        // Add a entry
        cache.put("key", "value");

        //cache.putForExternalRead(UUID.randomUUID().toString(), cacheMan.getAddress());
        //cache.put(UUID.randomUUID().toString(), cacheMan.getAddress());

// Validate the entry is now in the cache
        assertEquals(1, cache.size());
        assertTrue(cache.containsKey("key"));
// Remove the entry from the cache
        Object v = cache.remove("key");
// Validate the entry is no longer in the cache
        assertEquals("value", v);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*

        //synchronize for display purposes
        synchronized(cache) {
            cacheMan.print(cache, System.out);
        }
        */

    }


}
