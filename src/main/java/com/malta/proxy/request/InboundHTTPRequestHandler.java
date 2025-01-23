package com.malta.proxy.request;

import com.malta.proxy.queue.CacheQueue;
import com.malta.proxy.queue.CacheQueueEntity;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Http web server handler implementation. Deserializer of inbound request
 */
public class InboundHTTPRequestHandler {

    private static final Logger LOGGER;
    private static final String SUCCESS_RESPONSE;
    private static final String FAILURE_RESPONSE;
    private static final int SOCKET_TIMEOUT;

    private Socket socket = null;

    static {
        LOGGER = Logger.getLogger(InboundHTTPRequestHandler.class.getName());
        LOGGER.setLevel(Level.WARNING);
        SUCCESS_RESPONSE = "HTTP/1.1 200 OK\r\n\r\n";
        FAILURE_RESPONSE = "HTTP/1.1 500 Internal Server Error\r\n\r\n";
        SOCKET_TIMEOUT = 2000;
    }

    public InboundHTTPRequestHandler setSocket(Socket socket) {
        this.socket = socket;
        return this;
    }

    public void run() {

        try (
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedInputStream streamReader = new BufferedInputStream(socket.getInputStream())
        ) {
            socket.setSoTimeout(SOCKET_TIMEOUT);

            // here is how to deal if streamReader is ready before socket actually can handle data
            while(streamReader.available() < 1) {
                Thread.yield();
            }

            // main data reading block
            StringBuilder request = new StringBuilder();
            while (streamReader.available() > 0) {
                request.append(new String(streamReader.readNBytes(streamReader.available()), StandardCharsets.UTF_8));
            }

            // push handled request to the queue
            boolean success = CacheQueue.getInstance().add(new CacheQueueEntity(new Date(), request.toString(),
                Thread.currentThread().getName(), socket.getInetAddress().getHostAddress()));

            // wrap up with socket and build the simple response
            writer.write((success ? SUCCESS_RESPONSE : FAILURE_RESPONSE) + new Date());
            writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Processing the message in handler: {0}", e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Closing the socket in handler: {0}", e.getMessage());
            }
        }
    }
}
