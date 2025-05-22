package com.github.cn2425g03.server.events;

public record SubmitImageEvent(String id, String bucketName, String blobName) {}

