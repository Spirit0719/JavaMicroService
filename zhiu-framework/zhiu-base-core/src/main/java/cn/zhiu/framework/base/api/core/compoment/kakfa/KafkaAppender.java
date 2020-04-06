package cn.zhiu.framework.base.api.core.compoment.kakfa;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.github.danielwegener.logback.kafka.KafkaAppenderConfig;
import com.github.danielwegener.logback.kafka.delivery.FailedDeliveryCallback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 自定义Appender
 * @param <E>
 */
public class KafkaAppender<E> extends KafkaAppenderConfig<E> {
    private static final String KAFKA_LOGGER_PREFIX = "org.apache.kafka.clients";
    public static final Logger logger = LoggerFactory.getLogger(KafkaAppender.class);
    private LazyProducer lazyProducer = null;
    private final AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();
    private final ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();

    //加载断路器
    CircuitBreaker circuitBreaker = CircuitBreakerFactory.buildCircuitBreaker("KafkaAppender-c", 3, 2, 20000);

    private final FailedDeliveryCallback<E> failedDeliveryCallback = new FailedDeliveryCallback<E>() {
        @Override
        public void onFailedDelivery(E evt, Throwable throwable) {
            aai.appendLoopOnAppenders(evt);
        }
    };

    public KafkaAppender() {
        // setting these as config values sidesteps an unnecessary warning (minor bug in KafkaProducer)
        addProducerConfigValue(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        addProducerConfigValue(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
    }

    @Override
    public void doAppend(E e) {
        ensureDeferredAppends();
        if (e instanceof ILoggingEvent && ((ILoggingEvent) e).getLoggerName().startsWith(KAFKA_LOGGER_PREFIX)) {
            deferAppend(e);
        } else {
            super.doAppend(e);
        }
    }

    @Override
    public void start() {
        //检查配置参数是否完整
        if (!checkPrerequisites()) return;
        //初始化producer bean
        lazyProducer = new LazyProducer();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (lazyProducer != null && lazyProducer.isInitialized()) {
            try {
                lazyProducer.get().close();
            } catch (KafkaException e) {
                this.addWarn("Failed to shut down kafka producer: " + e.getMessage(), e);
            }
            lazyProducer = null;
        }
    }

    @Override
    public void addAppender(Appender<E> newAppender) {
        aai.addAppender(newAppender);
    }

    @Override
    public Iterator<Appender<E>> iteratorForAppenders() {
        return aai.iteratorForAppenders();
    }

    @Override
    public Appender<E> getAppender(String name) {
        return aai.getAppender(name);
    }

    @Override
    public boolean isAttached(Appender<E> appender) {
        return aai.isAttached(appender);
    }

    @Override
    public void detachAndStopAllAppenders() {
        aai.detachAndStopAllAppenders();
    }

    @Override
    public boolean detachAppender(Appender<E> appender) {
        return aai.detachAppender(appender);
    }

    @Override
    public boolean detachAppender(String name) {
        return aai.detachAppender(name);
    }

    @Override
    protected void append(E e) {
        // encode 逻辑
        final byte[] payload = encoder.encode(e);
        final byte[] key = keyingStrategy.createKey(e);
        final ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(topic, key, payload);

        // 如果还是熔断状态 则不初始化producer 直接走失败处理
        if (circuitBreaker.isOpen()) {
            failedDeliveryCallback.onFailedDelivery(e, null);
            return;
        }
        //初始化producer
        Producer producer = lazyProducer.get();
        if (producer == null) {
            //初始化失败 错误+1 并且走熔断
            circuitBreaker.actFailed();
            logger.error("kafka producer is null");
            failedDeliveryCallback.onFailedDelivery(e, null);
            return;
        }
        //成功一次 success+1
        circuitBreaker.actSuccess();
        // 核心发送方法
        deliveryStrategy.send(lazyProducer.get(), record, e, failedDeliveryCallback);
    }

    protected Producer<byte[], byte[]> createProducer() {
        return new KafkaProducer<>(new HashMap<>(producerConfig));
    }

    private void deferAppend(E event) {
        queue.add(event);
    }
    // drains queue events to super

    private void ensureDeferredAppends() {
        E event;
        while ((event = queue.poll()) != null) {
            super.doAppend(event);
        }
    }

    private class LazyProducer {
        private volatile Producer<byte[], byte[]> producer;
        private boolean initialized;

        public Producer<byte[], byte[]> get() {
            Producer<byte[], byte[]> result = this.producer;
            if (result == null) {
                synchronized (this) {
                    if (!initialized) {
                        result = this.producer;
                        if (result == null) { // 注意 这里initialize可能失败，比如传入servers为非法字符，返回producer为空，所以只用initialized标记来确保不进行重复初始化，而避免不断出错的初始化
                            this.producer = result = this.initialize();
                            initialized = true;
                        }
                    }
                }
            }
            return result;
        }

        protected Producer<byte[], byte[]> initialize() {
            Producer<byte[], byte[]> producer = null;
            try {
                producer = createProducer();
            } catch (Exception e) {
                addError("error creating producer", e);
            }
            return producer;
        }

        public boolean isInitialized() {
            return producer != null;
        }
    }
}

