/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.dobest.irondb.metastore.syncbrodcast;

import com.dobest.irondb.metastore.IronDBContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProjectManager {
    private static final Logger logger = LoggerFactory.getLogger(ProjectManager.class);
    private static final ConcurrentMap<IronDBContext, ProjectManager> CACHE = new ConcurrentHashMap<IronDBContext, ProjectManager>();

    public static ProjectManager getInstance(IronDBContext config) {
        ProjectManager r = CACHE.get(config);
        if (r != null) {
            return r;
        }

        synchronized (ProjectManager.class) {
            r = CACHE.get(config);
            if (r != null) {
                return r;
            }
            try {
                r = new ProjectManager(config);
                CACHE.put(config, r);
                if (CACHE.size() > 1) {
                    logger.warn("More than one singleton exist");
                }
                return r;
            } catch (IOException e) {
                throw new IllegalStateException("Failed to init ProjectManager from " + config, e);
            }
        }
    }

    public static void clearCache() {
        CACHE.clear();
    }

    // ============================================================================

    private IronDBContext config;
    // project name => ProjrectInstance

    private ProjectManager(IronDBContext config) throws IOException {
        logger.info("Initializing ProjectManager with metadata url " + config);
        this.config = config;

        // touch lower level metadata before registering my listener
        Broadcaster.getInstance(config).registerListener(new ProjectSyncListener(), "project");
    }

    public enum Event {

        CREATE("create"), UPDATE("update"), DROP("drop");
        private String text;

        Event(String text) {
            this.text = text;
        }

        public String getType() {
            return text;
        }

        public static Event getEvent(String event) {
            for (Event one : values()) {
                if (one.getType().equalsIgnoreCase(event)) {
                    return one;
                }
            }

            return null;
        }
    }



    private class ProjectSyncListener extends Broadcaster.Listener {

        @Override
        public void onClearAll(Broadcaster broadcaster) throws IOException {
            clearCache();
        }

        public void onEntityChange(Broadcaster broadcaster, String entity, Event event, String cacheKey)
                throws IOException {
            String project = cacheKey;

            if (event == Event.DROP) {
                return;
            }

            broadcaster.notifyProjectSchemaUpdate(project);
            broadcaster.notifyProjectDataUpdate(project);
        }
    }

    IronDBContext getConfig() {
        return config;
    }


    private String norm(String project) {
        return project;
    }

}
