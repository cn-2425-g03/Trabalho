package com.github.cn2425g03.server.services;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

public class CloudStorageService {

    private final Storage storage;

    public CloudStorageService(Storage storage) {
        this.storage = storage;
    }

    /**
     *
     * Create a bucket given a name, storage class and location
     *
     * @param bucketName bucket name
     * @param storageClass storage class
     * @param location location
     *
     * @return the bucket
     *
     */

    public Bucket createBucket(String bucketName, StorageClass storageClass, String location) {
        return storage.create(
                BucketInfo.newBuilder(bucketName)
                        .setLocation(location)
                        .setStorageClass(storageClass)
                        .build()
        );
    }

    /**
     *
     * Retrieves the bucket by its name.
     *
     * @param bucketName bucket name
     *
     * @return an Optional containing the bucket, or empty if it does not exist
     *
     */

    public Optional<Bucket> getBucket(String bucketName) {
        return Optional.ofNullable(storage.get(bucketName));
    }

    /**
     *
     * Create a blob in the bucket given the bucket, blob name and the bytes to upload
     *
     * @param bucket bucket
     * @param blobName blob name
     * @param bytes bytes array
     *
     */

    public void uploadBlobToBucket(Bucket bucket, String blobName, byte[] bytes) throws IOException {

        BlobId blobId = BlobId.of(bucket.getName(), blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .build();

        if (bytes.length > 1_000_000) {

            try (WriteChannel writer = storage.writer(blobInfo); InputStream stream = new ByteArrayInputStream(bytes)) {

                byte[] buffer = new byte[1024];
                int limit;

                while((limit = stream.read(buffer)) >= 0){
                    writer.write(ByteBuffer.wrap(buffer, 0, limit));
                }

            }

            return;
        }

        storage.create(blobInfo, bytes);
    }

}
