package com.malta.proxy.request;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class InboundHTTPRequestEntity {
    private final Long order;
    private final Date receivedAt;
    private final String sourceIPAddress;
    private final String method;
    private final String URI;
    private final String protocolVersion;
    private final Map<String, String> headers;
    private final String body;
    private final String threadNumber;

    @Override
    public String toString() {
        return "InboundHTTPRequestEntity{" +
                "order=" + order +
                ", receivedAt=" + receivedAt +
                ", sourceIPAddress='" + sourceIPAddress + '\'' +
                ", method='" + method + '\'' +
                ", URI='" + URI + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", threadNumber='" + threadNumber + '\'' +
                '}';
    }

    public InboundHTTPRequestEntity(Long order, Date receivedAt, String sourceIPAddress, String method, String URI,
        String protocolVersion, Map<String, String> headers, String body, String threadNumber) {
        this.order = order;
        this.receivedAt = receivedAt;
        this.sourceIPAddress = sourceIPAddress;
        this.method = method;
        this.URI = URI;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
        this.body = body;
        this.threadNumber = threadNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InboundHTTPRequestEntity that = (InboundHTTPRequestEntity) o;
        if (!sourceIPAddress.equals(that.sourceIPAddress)) return false;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        int result = receivedAt.hashCode();
        result = 31 * result + sourceIPAddress.hashCode();
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

}
