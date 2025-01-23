package com.malta.proxy.request;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public interface InboundHTTPRequestDeserializer {
    /**
     * Basically break down the request message to request line, headers and body payload
     * according to the https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
     *
     * @param request the request
     * @return the inbound http request entity
     */
    static InboundHTTPRequestEntity deserialize(
        String request, Date receivedAt, String threadName, String inetAddress) {

        String[] requestParts = request.split("\r\n\r\n",2);
        String[] requestLine = requestParts[0].lines().findFirst().orElse("").split(" ");

        Map<String, String> headerLinesMap = new HashMap<>();
        requestParts[0].lines().skip(1).forEach(headerLine ->
            headerLinesMap.put(headerLine.split(":")[0], headerLine.split(":")[1]));

        return new InboundHTTPRequestEntityBuilder().with(req -> {
            // request line
            req.method = requestLine[0];
            req.URI = requestLine[1];
            req.protocolVersion = requestLine[2];
            // map of headers
            req.headers = headerLinesMap;
            // body payload
            req.body = requestParts[1];
            // meta info
            req.receivedAt = receivedAt;
            req.threadNumber = threadName;
            req.sourceIPAddress = inetAddress;
        }).createInboundHTTPRequestEntity();

    }
}
