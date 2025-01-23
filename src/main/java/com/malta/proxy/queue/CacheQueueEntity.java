package com.malta.proxy.queue;

import java.util.Date;
import java.util.Objects;

public class CacheQueueEntity {

    final Date createdAt;
    final String request;
    final String threadName;
    final String inetAddress;

    public CacheQueueEntity(Date createdAt, String request, String threadName, String inetAddress) {
        this.createdAt = createdAt;
        this.request = request;
        this.threadName = threadName;
        this.inetAddress = inetAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheQueueEntity that = (CacheQueueEntity) o;
        if (!Objects.equals(createdAt, that.createdAt)) return false;
        return Objects.equals(request, that.request);
    }

    @Override
    public int hashCode() {
        int result = createdAt != null ? createdAt.hashCode() : 0;
        result = 31 * result + (request != null ? request.hashCode() : 0);
        return result;
    }
}
