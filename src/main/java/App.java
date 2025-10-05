import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

public class App {
    public static void main(String[] args) {
        // The S3 key (or any content) you want to send as a message
        String messageContent = "Image key: sample.jpg";

        // Your SNS topic ARN
        String topicArn = "arn:aws:sns:eu-north-1:123456789012:myTopic";

        // Create the SNS client (must be same region as topic)
        Region region = Region.EU_NORTH_1;
        try (SnsClient snsClient = SnsClient.builder().region(region).build()) {

            // Create and send the message
            PublishRequest request = PublishRequest.builder()
                    .message(messageContent)
                    .topicArn(topicArn)
                    .build();

            PublishResponse result = snsClient.publish(request);

            System.out.println("✅ Message sent successfully!");
            System.out.println("Message ID: " + result.messageId());
        } catch (Exception e) {
            System.err.println("❌ Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
