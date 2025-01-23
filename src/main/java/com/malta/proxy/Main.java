package com.malta.proxy;

import java.util.*;
import java.util.stream.Stream;

public class Main {

    public static final Map<ARGUMENT, String> ARGUMENTS = new EnumMap<>(ARGUMENT.class);

    // enum-definition of possible arg list
    public enum ARGUMENT {
        SERVER_PORT("8081"),            // which port app will listen to
        THREADS("4"),                   // how many threads will have the handlers pool
        ECHO_MODE("true"),              // worker mode: log the request
        PROXY_MODE("false"),            // worker mode: forward the request
        PROXY_TARGET("127.0.0.1");      // URI endpoint where the request should be forwarded

        private final String defaultValue;

        ARGUMENT(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    public static void main(String[] args) {

        // fill the arguments from CLI args
        Arrays.stream(ARGUMENT.values()).forEach(arg -> ARGUMENTS.put(arg, arg.defaultValue));
        Arrays.stream(args)
            .filter(arg -> {
                if (arg.split("=").length < 2) return false;
                return Stream.of(ARGUMENT.values()).anyMatch(v -> v.name().equals(arg.split("=")[0]));
            })
            .forEach(arg -> ARGUMENTS.put(ARGUMENT.valueOf(arg.split("=")[0]), arg.split("=")[1]));

        // run the pool of handlers
        new HTTPServer();
    }

}
