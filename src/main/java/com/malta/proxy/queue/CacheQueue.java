package com.malta.proxy.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Cache queue basic implementation
 */
public class CacheQueue {

    private static final Logger LOGGER;
    private static final CacheQueue INSTANCE;
    // size() is too expensive method as it iterates through all collection
    private final AtomicInteger counter;
    // we have multiple publishers so non-blocking CAS is a pros
    private final ConcurrentLinkedQueue<CacheQueueEntity> cacheCommonQueue;
    private CacheQueueProcessor cacheQueueProcessor;

    static {
        LOGGER = Logger.getLogger(CacheQueue.class.getName());
        LOGGER.setLevel(Level.WARNING);
        INSTANCE = new CacheQueue();
    }

    private CacheQueue() {
        counter = new AtomicInteger(0);
        cacheCommonQueue  = new ConcurrentLinkedQueue<>();
    }

    public void setCacheQueueProcessor(CacheQueueProcessor cacheQueueProcessor) {
        this.cacheQueueProcessor = cacheQueueProcessor;
    }

    public static CacheQueue getInstance() {
        return INSTANCE;
    }

    /**
     * Add request entity to the queue. Increments queue requests counter and trigger injected queue processor
     *
     * @param cacheQueueEntity - the tiny HTTP request entity
     */
    public boolean add(CacheQueueEntity cacheQueueEntity) {
        if(cacheCommonQueue.add(cacheQueueEntity)) {
            counter.incrementAndGet();
            cacheQueueProcessor.process();
            LOGGER.log(Level.INFO, "Requests count: {0}",  counter.get());
            return true;
        }
        return false;
    }

    public CacheQueueEntity poll() {
        CacheQueueEntity cacheQueueEntity = cacheCommonQueue.poll();
        if(cacheQueueEntity != null) {
            counter.decrementAndGet();
        }
        LOGGER.log(Level.INFO, "Requests count: {0}", counter.get());
        return cacheQueueEntity;
    }

    public AtomicInteger getCounter() {
        return counter;
    }
}
