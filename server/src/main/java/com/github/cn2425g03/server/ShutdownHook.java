package com.github.cn2425g03.server;

import io.grpc.Server;

public class ShutdownHook extends Thread {

    private final Server server;

    public ShutdownHook(Server server) {
        this.server = server;
    }

    @Override
    public void run() {

        System.err.println("*shutdown gRPC server, because JVM is shutting down");

        try {
            server.shutdown();
            server.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        System.err.println("*** server shut down");
    }

}
