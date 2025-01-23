package com.malta.proxy;

import com.malta.proxy.request.InboundHTTPRequestHandler;
import com.malta.proxy.queue.CacheQueue;
import com.malta.proxy.queue.CacheQueueSingleThreadWorker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.malta.proxy.Main.ARGUMENTS;

/**
 * HTTP server dispatcher
 */
public class HTTPServer {

    private static final Logger LOGGER;
    private static final int SERVER_THREAD_COUNT;
    private static final int SERVER_PORT;
    private static final InetSocketAddress INET_SOCKET_ADDRESS;

    private volatile boolean isStopped = false;
    public final Map<String, InboundHTTPRequestHandler> handlerPool = new HashMap<>();

    static {
        LOGGER = Logger.getLogger(Main.class.getName());
        SERVER_THREAD_COUNT = Integer.parseInt(ARGUMENTS.get(Main.ARGUMENT.THREADS));
        SERVER_PORT = Integer.parseInt(ARGUMENTS.get(Main.ARGUMENT.SERVER_PORT));
        INET_SOCKET_ADDRESS = new InetSocketAddress("localhost", SERVER_PORT);
    }

    public HTTPServer() {
        start();
    }

    void start() {
        // inject "cache queue single thread processor" implementation to the cache queue
        CacheQueue.getInstance().setCacheQueueProcessor(CacheQueueSingleThreadWorker.getInstance());

        // service executor for parent thread
        ExecutorService generalExecutorService = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setPriority(10);
            thread.setName("MALTA_MAIN");
            return thread;
        });

        // service executor for child threads, socket handlers
        ExecutorService workerExecutorService = Executors.newFixedThreadPool(SERVER_THREAD_COUNT, new ThreadFactory() {
            private final AtomicInteger instanceCount = new AtomicInteger();
            @Override
            public Thread newThread(Runnable runnable)
            {
                Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                thread.setDaemon(true);
                thread.setPriority(1);
                thread.setName("M_" + instanceCount.getAndIncrement());
                return thread;
            }
        });

        poolHeater(workerExecutorService);

        LOGGER.log(Level.INFO, "MaltaProxy 0.1\r\n\r\nThreads: {0}\r\nServer started on port: {1} ...\n",
            new String[] {String.valueOf(SERVER_THREAD_COUNT), String.valueOf(SERVER_PORT)});

        CompletableFuture.runAsync(() -> {
            try (ServerSocket serverSocket = new ServerSocket()) {
                serverSocket.bind(INET_SOCKET_ADDRESS);
                while (!isStopped) {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setTcpNoDelay(true);
                    CompletableFuture.runAsync(() ->
                        handlerPool.get(Thread.currentThread().getName()).setSocket(clientSocket).run(), workerExecutorService);
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Running task in thread from poller: {0}", e.getMessage());
            }
        }, generalExecutorService);

    }

    /**
     * Pool heater - initialization for the pool of handlers
     *
     * @param executorService - to fill the map with actual thread names we need same ThreadFactory
     */
    void poolHeater(ExecutorService executorService) {
        for (int i = 0; i < SERVER_THREAD_COUNT; i++) {
            executorService.execute(() ->
                handlerPool.put(Thread.currentThread().getName(), new InboundHTTPRequestHandler()));
        }
    }
}
