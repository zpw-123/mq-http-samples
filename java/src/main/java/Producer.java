import com.aliyun.mq.http.MQClient;
import com.aliyun.mq.http.MQProducer;
import com.aliyun.mq.http.model.TopicMessage;
import src.main.java.ProducerSingleton.ProducerSingleton;

import java.util.Date;

public class Producer {

    public static void main(String[] args) {
        MQClient mqClient = new MQClient(
                // 设置HTTP接入域名（此处以公共云生产环境为例）
                "${HTTP_ENDPOINT}",
                // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
                "${ACCESS_KEY}",
                // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
                "${SECRET_KEY}"
        );

        // 所属的 Topic
        final String topic = "${TOPIC}";
        // Topic所属实例ID，默认实例为空
        final String instanceId = "${INSTANCE_ID}";

        // 获取Topic的生产者
        MQProducer producer = ProducerSingleton.getOneProducer(mqClient, instanceId, topic);

        try {
            // 循环发送4条消息
            for (int i = 0; i < 4; i++) {
                TopicMessage pubMsg;
                if (i % 2 == 0) {
                    // 普通消息
                    pubMsg = new TopicMessage(
                            // 消息内容
                            "hello mq!".getBytes(),
                            // 消息标签
                            "A"
                    );
                    // 设置属性
                    pubMsg.getProperties().put("a", String.valueOf(i));
                    // 设置KEY
                    pubMsg.setMessageKey("MessageKey");
                } else {
                    pubMsg = new TopicMessage(
                            // 消息内容
                            "hello mq!".getBytes(),
                            // 消息标签
                            "A"
                    );
                    // 设置属性
                    pubMsg.getProperties().put("a", String.valueOf(i));
                    // 定时消息, 定时时间为10s后
                    pubMsg.setStartDeliverTime(System.currentTimeMillis() + 10 * 1000);
                }
                // 同步发送消息，只要不抛异常就是成功
                TopicMessage pubResultMsg = producer.publishMessage(pubMsg);

                // 同步发送消息，只要不抛异常就是成功
                System.out.println(new Date() + " Send mq message success. Topic is:" + topic + ", msgId is: " + pubResultMsg.getMessageId()
                    + ", bodyMD5 is: " + pubResultMsg.getMessageBodyMD5());
            }
        } catch (Throwable e) {
            // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
            System.out.println(new Date() + " Send mq message failed. Topic is:" + topic);
            e.printStackTrace();
        }

        mqClient.close();
    }

}
