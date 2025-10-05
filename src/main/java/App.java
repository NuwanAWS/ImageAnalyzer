import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.nio.file.Paths;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws Exception {
        String bucket = "image-bucket-4-me";   // your S3 bucket
        String key = "sample.jpg";        // image key in S3
        String phoneNumber = "+94773333573"; // E.164 format for SNS

        // 1. Download image from S3
        S3Client s3 = S3Client.create();
        String localFile = "/tmp/" + key;
        s3.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(), Paths.get(localFile));

        // 2. Analyze image with Rekognition
        RekognitionClient rekog = RekognitionClient.create();
        DetectLabelsResponse response = rekog.detectLabels(DetectLabelsRequest.builder()
                .image(Image.builder().s3Object(b -> b.bucket(bucket).name(key)).build())
                .maxLabels(5)
                .build());

        String summary = response.labels().stream()
                .map(Label::name)
                .collect(Collectors.joining(", "));

        System.out.println("Image summary: " + summary);

        // 3. Send SMS via SNS
        SnsClient sns = SnsClient.create();
        sns.publish(PublishRequest.builder()
                .message("Image summary: " + summary)
                .phoneNumber(phoneNumber)
                .build());

        System.out.println("SNS message sent to " + phoneNumber);
    }
}
