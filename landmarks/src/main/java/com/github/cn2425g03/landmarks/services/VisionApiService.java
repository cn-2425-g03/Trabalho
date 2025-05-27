package com.github.cn2425g03.landmarks.services;

import com.google.cloud.vision.v1.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VisionApiService {

    /**
     *
     * Retrieves all the landmarks information given a bucket name and blob name
     *
     * @param bucketName bucket name
     * @param blobName blob name
     *
     * @return a list of all information about the landmark
     */

    public List<EntityAnnotation> getLandmarkInformation(String bucketName, String blobName) {

        String path = "gs://" + bucketName + "/" + blobName;

        List<AnnotateImageRequest> requests = new ArrayList<>();

        ImageSource imageSource = ImageSource.newBuilder()
                .setGcsImageUri(path)
                .build();

        Image image = Image.newBuilder()
                .setSource(imageSource)
                .build();

        Feature feature = Feature.newBuilder()
                .setType(Feature.Type.LANDMARK_DETECTION)
                .build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feature)
                .setImage(image)
                .build();

        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {

            BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = batchResponse.getResponsesList();

            for (AnnotateImageResponse response : responses) {

                if (response.hasError())
                    throw new RuntimeException(response.getError().getMessage());

                 return response.getLandmarkAnnotationsList();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Could not find landmark information");
    }

}
