package com.github.cn2425g03.landmarks.models;

public record ImageInformation(
        String id, String bucketName, String blobName, double latitude,
        double longitude, double score
) {}
