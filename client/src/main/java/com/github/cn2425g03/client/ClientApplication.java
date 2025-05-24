package com.github.cn2425g03.client;

import com.github.cn2425g03.client.services.ImageService;
import image.ImageGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class ClientApplication {

    private final static String HOSTNAME = "localhost";
    private final static int PORT = 8080;

    public static void main(String[] args) {

        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(HOSTNAME, PORT)
                .usePlaintext()
                .build();

        ImageGrpc.ImageStub stub = ImageGrpc.newStub(managedChannel);
        ImageService imageService = new ImageService(stub);
        Scanner scanner = new Scanner(System.in);

        while (true) {

            int option = menu();
            switch (option) {
                case 1:
                    imageService.submitImage(read("Filename: ", scanner));
                    break;
                case 2:
                    imageService.getImageInformationById(
                            read("Image ID: ", scanner), read("Filename: ", scanner)
                    );
                    break;
                case 99:
                    System.exit(0);
            }

        }

    }

    private static int menu() {

        int option;
        Scanner scanner = new Scanner(System.in);

        do {

            System.out.println();
            System.out.println("Please choose one of the following options:");
            System.out.println();
            System.out.println("1 - Upload File");
            System.out.println("2 - Retrieve Image Information");
            System.out.println("99 - Exit");
            System.out.println();
            option = scanner.nextInt();

        }while(!(option >= 1 && option <= 2) && option != 99);

        return option;
    }

    private static String read(String msg, Scanner input) {
        System.out.println(msg);
        return input.nextLine();
    }

}
