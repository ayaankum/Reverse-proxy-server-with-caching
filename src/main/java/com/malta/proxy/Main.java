package com.malta.proxy;

import java.util.*;

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
        
        // Fill the arguments from default values
        for (ARGUMENT arg : ARGUMENT.values()) {
            ARGUMENTS.put(arg, arg.defaultValue);
        }

        // Process CLI args
        for (String arg : args) {
            String[] parts = arg.split("=");
            if (parts.length >= 2) {
                String argName = parts[0];
                String argValue = parts[1];
                // Check if the argument name matches any defined enum
                for (ARGUMENT v : ARGUMENT.values()) {
                    if (v.name().equals(argName)) {
                        ARGUMENTS.put(v, argValue);
                    }
                }
            }
        }

        // Run the pool of handlers
        new HTTPServer();
    }
}
