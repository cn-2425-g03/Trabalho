package com.github.cn2425g03.server.services;

import com.github.cn2425g03.server.repositories.ImageInformationRepository;
import com.google.pubsub.v1.Topic;
import com.google.storage.v2.Bucket;
import image.ImageIdentifier;
import image.ImageInformation;
import image.MonumentDetection;
import image.Score;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageInformationRepository imageInformationRepository;

    @Mock
    private PubSubService pubSubService;

    @Mock
    private CloudStorageService cloudStorageService;

    @Mock
    private Bucket bucket;

    @Mock
    private Topic topic;

    @InjectMocks
    private ImageService imageService;

    @Test
    @SuppressWarnings("unchecked")
    public void get_image_information_by_id_that_does_not_exist_should_fail() throws ExecutionException, InterruptedException {

        when(imageInformationRepository.getById(anyString())).thenReturn(List.of());
        StreamObserver<ImageInformation> responseObserver = mock(StreamObserver.class);

        imageService.getImageInformation(
                ImageIdentifier.newBuilder()
                        .setId("1")
                        .build(),
                responseObserver);

        verify(responseObserver, times(0)).onNext(any());
        verify(responseObserver, times(0)).onCompleted();
        verify(responseObserver, times(1)).onError(any());
    }


    @Test
    @SuppressWarnings("unchecked")
    public void get_image_information_by_id_that_exists_should_succeed() throws ExecutionException, InterruptedException {

        var torre = new com.github.cn2425g03.server.models.ImageInformation(
                "1", "Torre Eiffel", "bucket", "torre",
                10.0, 20.0, 0.90
        );

        var arco = new com.github.cn2425g03.server.models.ImageInformation(
                "1", "Arco do Triunfo", "bucket", "torre",
                10.0, 20.0, 0.90
        );

        when(imageInformationRepository.getById(anyString()))
                .thenReturn(List.of(torre, arco)); //two monuments in the same picture

        StreamObserver<ImageInformation> responseObserver = mock(StreamObserver.class);

        imageService.getImageInformation(
                ImageIdentifier.newBuilder()
                        .setId("1")
                        .build(),
                responseObserver);

        ArgumentCaptor<ImageInformation> captor = ArgumentCaptor.forClass(ImageInformation.class);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver, times(1)).onCompleted();
        verify(responseObserver, times(0)).onError(any());

        ImageInformation capturedResponse = captor.getValue();
        assertEquals(2, capturedResponse.getResultsCount());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void when_there_is_no_image_with_score_higher_than_should_succeed() throws ExecutionException, InterruptedException {

        when(imageInformationRepository.getAllByScoreGreaterThan(anyDouble()))
                .thenReturn(List.of());
        StreamObserver<MonumentDetection> responseObserver = mock(StreamObserver.class);

        imageService.getAllImagesDetection(
                Score.newBuilder()
                        .setValue(0.8)
                        .build(),
                responseObserver
        );

        verify(responseObserver, times(0)).onNext(any());
        verify(responseObserver, times(1)).onCompleted();
        verify(responseObserver, times(0)).onError(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void when_there_is_an_image_with_score_higher_than_should_succeed() throws ExecutionException, InterruptedException {

        var torre = new com.github.cn2425g03.server.models.ImageInformation(
                "1", "Torre Eiffel", "bucket", "torre",
                10.0, 20.0, 0.90
        );

        var arco = new com.github.cn2425g03.server.models.ImageInformation(
                "2", "Arco do Triunfo", "bucket", "torre",
                10.0, 20.0, 0.90
        );

        when(imageInformationRepository.getAllByScoreGreaterThan(anyDouble()))
                .thenReturn(List.of(torre, arco));
        StreamObserver<MonumentDetection> responseObserver = mock(StreamObserver.class);

        imageService.getAllImagesDetection(
                Score.newBuilder()
                        .setValue(0.8)
                        .build(),
                responseObserver
        );

        verify(responseObserver, times(2)).onNext(any());
        verify(responseObserver, times(1)).onCompleted();
        verify(responseObserver, times(0)).onError(any());
    }

}