package com.github.cn2425g03.server;

import com.github.cn2425g03.server.repositories.ImageInformationRepository;
import com.github.cn2425g03.server.services.CloudStorageService;
import com.github.cn2425g03.server.services.ImageService;
import com.github.cn2425g03.server.services.PubSubService;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import com.google.pubsub.v1.Topic;
import io.grpc.ServerBuilder;

public class GrpcApplication {

    private final static int PORT = 8080;
    private final static String PROJECT_ID = "cn2425-t3-g03";
    private final static String TOPIC_NAME = "cn2425-proj-g03";
    private final static String BUCKET_NAME = "cn2425-proj-g03";
    private final static String DATABASE_ID = "cn2425-g03-trabalho";
    private final static String API_KEY = "AIzaSyAZguhOSGbeioCyRdbu8ut-I-yBgh02rAU";

    public static void main(String[] args) {

        try {

            StorageOptions storageOptions = StorageOptions.getDefaultInstance();
            Storage storage = storageOptions.getService();
            PubSubService pubSubService = new PubSubService(PROJECT_ID);

            Topic topic = pubSubService.getTopicByName(TOPIC_NAME)
                    .orElseGet(
                            () -> pubSubService.createTopic(TOPIC_NAME)
                    );

            CloudStorageService cloudStorageService = new CloudStorageService(storage);

            Bucket bucket = cloudStorageService.getBucket(BUCKET_NAME)
                    .orElseGet(
                            () -> cloudStorageService.createBucket(
                                    BUCKET_NAME,
                                    StorageClass.STANDARD,
                                    "europe-west1"
                            )
                    );

            FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                    .setDatabaseId(DATABASE_ID)
                    .build();

            Firestore database = firestoreOptions.getService();
            ImageInformationRepository imageInformationRepository = new ImageInformationRepository(database);

            io.grpc.Server server = ServerBuilder.forPort(PORT)
                    .addService(
                            new ImageService(
                                    imageInformationRepository, pubSubService, cloudStorageService,
                                    bucket, topic, API_KEY
                            )
                    )
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
