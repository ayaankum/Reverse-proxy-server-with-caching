package com.malta.proxy.request;

import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The convenient builder class for InboundHTTPRequestEntity
 */
public class InboundHTTPRequestEntityBuilder {

    public Long order;
    public Date receivedAt;
    public String sourceIPAddress;
    public String method;
    public String URI;
    public String protocolVersion;
    public Map<String, String> headers;
    public String body;
    public String threadNumber;

    public InboundHTTPRequestEntityBuilder with(
            Consumer<InboundHTTPRequestEntityBuilder> builderMethod) {
        builderMethod.accept(this);
        return this;
    }

    public InboundHTTPRequestEntity createInboundHTTPRequestEntity() {
        return new InboundHTTPRequestEntity(
            order, receivedAt, sourceIPAddress, method, URI, protocolVersion, headers, body, threadNumber);
    }
}