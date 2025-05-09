package com.github.cn2425g03.server;

import com.github.cn2425g03.server.services.ImageService;
import io.grpc.ServerBuilder;

public class GrpcApplication {

    private final static int PORT = 8080;

    public static void main(String[] args) {

        try {

            io.grpc.Server server = ServerBuilder.forPort(PORT)
                    .addService(new ImageService())
                    .build();

            server.start();
            System.out.println("Server started on port " + PORT);

            Runtime.getRuntime().addShutdownHook(new ShutdownHook(server));
            server.awaitTermination();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
