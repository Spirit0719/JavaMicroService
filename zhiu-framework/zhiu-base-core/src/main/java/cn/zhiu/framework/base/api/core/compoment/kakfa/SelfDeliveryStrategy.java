package cn.zhiu.framework.base.api.core.compoment.kakfa;

import com.github.danielwegener.logback.kafka.delivery.DeliveryStrategy;
import com.github.danielwegener.logback.kafka.delivery.FailedDeliveryCallback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfDeliveryStrategy implements DeliveryStrategy {
    private final static Logger logger = LoggerFactory.getLogger(SelfDeliveryStrategy.class);
    CircuitBreaker circuitBreaker = CircuitBreakerFactory.buildCircuitBreaker("KafkaAppender-c", 3, 2, 20000);

    public <K, V, E> boolean send(Producer<K, V> producer, ProducerRecord<K, V> record, final E event, final FailedDeliveryCallback<E> failedDeliveryCallback) {
        if (circuitBreaker.isNotOpen()) {
            try {
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        circuitBreaker.actFailed();
                        failedDeliveryCallback.onFailedDelivery(event, exception);
                        logger.error("kafka producer send log error", exception);
                    } else {
                        circuitBreaker.actSuccess();
                    }
                });
                return true;
            } catch (KafkaException e) {
                circuitBreaker.actFailed();
                failedDeliveryCallback.onFailedDelivery(event, e);
                logger.error("kafka send log error", e);
                return false;
            }
        } else {
            logger.error("kafka log circuitBreaker open");
            return false;
        }
    }

}
