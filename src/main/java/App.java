import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        // ---- CONFIG ----
        String bucketName = "image-bucket-4-me";   // ✅ your actual bucket name
        String key = "sample.jpg";        // ✅ the object key in S3
        String phoneNumber = "+94773333573";  // ✅ use E.164 format for SNS

        // ---- REGION SETUP ----
        // Rekognition is in eu-west-1 (Ireland)
        Region rekognitionRegion = Region.EU_WEST_1;
        // Use the same region for SNS for simplicity
        Region snsRegion = Region.EU_WEST_1;

        // If your S3 bucket is in a *different* region (eu-north-1):
        Region s3Region = Region.EU_NORTH_1;  // ✅ adjust to your bucket’s region

        // ---- CLIENTS ----
        S3Client s3 = S3Client.builder()
                .region(s3Region)
                .build();

        RekognitionClient rekognition = RekognitionClient.builder()
                .region(rekognitionRegion)
                .build();

        SnsClient sns = SnsClient.builder()
                .region(snsRegion)
                .build();

        // ---- DOWNLOAD IMAGE ----
        Path localPath = Paths.get("/tmp/" + key);
        s3.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build(), localPath);

        // ---- REKOGNITION LABEL DETECTION ----
        DetectLabelsRequest request = DetectLabelsRequest.builder()
                .image(Image.builder().s3Object(S3Object.builder()
                        .bucket(bucketName)
                        .name(key)
                        .build())
                        .build())
                .maxLabels(5)
                .build();

        DetectLabelsResponse response = rekognition.detectLabels(request);

        StringBuilder summary = new StringBuilder("Detected labels:\n");
        response.labels().forEach(label -> {
            summary.append(label.name())
                    .append(" (Confidence: ")
                    .append(String.format("%.2f", label.confidence()))
                    .append("%)\n");
        });

        // ---- SNS NOTIFICATION ----
        sns.publish(PublishRequest.builder()
                .message(summary.toString())
                .phoneNumber(phoneNumber)
                .build());

        System.out.println("✅ Image analyzed and SNS message sent!");
    }
}
