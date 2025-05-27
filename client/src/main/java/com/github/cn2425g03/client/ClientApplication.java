package com.github.cn2425g03.client;

import com.github.cn2425g03.client.services.ImageService;
import com.google.gson.Gson;
import image.ImageGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class ClientApplication {

    private final static String LOOKUP_URI = "https://europe-west1-cn2425-t3-g03.cloudfunctions.net/lookup";
    private final static int PORT = 8080;

    public static void main(String[] args) {

        String serverIp = Arrays.asList(args).contains("--preferred-ip") ? getServerIp() : getRandomServerIp();

        System.out.println("Connecting to " + serverIp + "...");

        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(serverIp, PORT)
                .usePlaintext()
                .build();

        ImageGrpc.ImageStub stub = ImageGrpc.newStub(managedChannel);
        ImageService imageService = new ImageService(stub);
        Scanner scanner = new Scanner(System.in);

        while (true) {

            int option = menu();

            try {

                switch (option) {
                    case 1:
                        imageService.submitImage(read("Filename: ", scanner));
                        break;
                    case 2:
                        imageService.getImageInformationById(
                                read("Image ID: ", scanner), read("Filename: ", scanner)
                        );
                        break;
                    case 3:
                        imageService.getAllImagesDetection(
                                Double.parseDouble(read("Score: ", scanner))
                        );
                        break;
                    case 99:
                        System.exit(0);
                }

            }catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

    }

    private static String getRandomServerIp() {

        String[] ips = getAllServersIp();

        return ips[new Random().nextInt(ips.length)];
    }

    private static String getServerIp() {

        String[] ips = getAllServersIp();

        System.out.println();
        System.out.println("Please choose one of the following servers:");
        System.out.println();

        for (String ip : ips) {
            System.out.println(ip);
        }

        System.out.println();

        String ip = read("Server IP: ", new Scanner(System.in));

        if (!Arrays.asList(ips).contains(ip)) {
            System.out.println("Invalid server IP!");
            System.exit(0);
        }

        return ip;
    }

    private static String[] getAllServersIp() {

        try (HttpClient httpClient = HttpClient.newHttpClient()) {

            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(LOOKUP_URI))
                    .GET()
                    .build();

            String json = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
            Gson gson = new Gson();

            return gson.fromJson(json, String[].class);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static int menu() {

        int option = 0;
        Scanner scanner = new Scanner(System.in);

        do {

            System.out.println();
            System.out.println("Please choose one of the following options:");
            System.out.println();
            System.out.println("1 - Upload File");
            System.out.println("2 - Retrieve Image Information");
            System.out.println("3 - Monument Detection");
            System.out.println("99 - Exit");
            System.out.println();

            try {
                option = scanner.nextInt();
            }catch (Exception e) {
                System.out.println("Please choose a valid option!");
                break;
            }

        }while(!(option >= 1 && option <= 3) && option != 99);

        return option;
    }

    private static String read(String msg, Scanner input) {
        System.out.println(msg);
        return input.nextLine();
    }

}
