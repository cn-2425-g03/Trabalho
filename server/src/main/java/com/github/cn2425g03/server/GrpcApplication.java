package com.github.cn2425g03.server;

import com.github.cn2425g03.server.services.CloudStorageService;
import com.github.cn2425g03.server.services.ImageService;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import io.grpc.ServerBuilder;

public class GrpcApplication {

    private final static int PORT = 8080;
    private final static String BUCKET_NAME = "cn2425-proj-g03";

    public static void main(String[] args) {

        try {

            StorageOptions storageOptions = StorageOptions.getDefaultInstance();
            Storage storage = storageOptions.getService();
            CloudStorageService cloudStorageService = new CloudStorageService(storage);
            Bucket bucket = cloudStorageService.getBucket(BUCKET_NAME);

            if (bucket == null)
                bucket = cloudStorageService.createBucket(BUCKET_NAME, StorageClass.STANDARD, "europe-west1");

            io.grpc.Server server = ServerBuilder.forPort(PORT)
                    .addService(new ImageService(cloudStorageService, bucket))
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
