import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Properties;



public class Consumer {
    public static void main(String[] args) {
        int reverseNumber;

        KafkaConsumer consumer;
        String topic = "reverse";
        String broker = "localhost:9092";
        Properties props = new Properties();
        props.put("bootstrap.servers", broker);
        props.put("group.id", "test-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList(topic));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(5000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.value());
                int num = Integer.parseInt(record.value());
                int reversed = 0;
                while (num != 0) {

                    // get last digit from num
                    int digit = num % 10;
                    reversed = reversed * 10 + digit;
                    // remove the last digit from num
                    num = num / 10;
                }
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/kafkaevendb", "root", "");
                    String sql = "INSERT INTO `reversenumbers`(`reversenumbers`) VALUES (?)";

                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setInt(1, reversed);
                    stmt.executeUpdate();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            }
        }
    }



