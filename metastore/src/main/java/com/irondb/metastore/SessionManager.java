package com.irondb.metastore;

import com.irondb.metastore.bean.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session Manager
 * Thread safe HashMap (Client IP : Token)
 */
public class SessionManager {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    // Thread safe HashMap (Client IP : Token)
    private final Map<String, Token> repo = new ConcurrentHashMap<>();

    public void saveSession(String clientId, Token session) {
        this.repo.put(clientId, session);
    }
    public Token getSession(String clientId) {
        return this.repo.get(clientId);
    }

    public Token removeSession(String clientId) {
        return this.repo.remove(clientId);
    }

    public boolean removeSession(String clientId, Token session) {
        return this.repo.remove(clientId, session);
    }
}
