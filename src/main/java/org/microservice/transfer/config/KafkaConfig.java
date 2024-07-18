package org.microservice.transfer.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final Environment env;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTransactionManager<String, Object> kafkaTransactionManager(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean("transactionManager")
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public NewTopic createWithdrawTopic() {
        return TopicBuilder
                .name(env.getProperty("withdraw-money-topic", "withdraw-money-topic"))
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic createDepositTopic() {
        return TopicBuilder
                .name(env.getProperty("deposit-money-topic", "deposit-money-topic"))
                .partitions(3)
                .replicas(3)
                .build();
    }

    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("spring.kafka.producer.bootstrap-servers"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                env.getProperty("spring.kafka.producer.key-serializer"));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                env.getProperty("spring.kafka.producer.value-serializer"));
        props.put(ProducerConfig.ACKS_CONFIG,
                env.getProperty("spring.kafka.producer.acks"));
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,
                env.getProperty("spring.kafka.producer.properties.delivery.timeout.ms"));
        props.put(ProducerConfig.LINGER_MS_CONFIG,
                env.getProperty("spring.kafka.producer.properties.linger"));
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,
                env.getProperty("spring.kafka.producer.properties.request.timeout.ms"));
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                env.getProperty("spring.kafka.producer.properties.enable.idempotence"));
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
                env.getProperty("spring.kafka.producer.properties.max.in.flight.requests.per.connection"));
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,
                env.getProperty("spring.kafka.producer.transaction-id-prefix"));

        return props;
    }
}
