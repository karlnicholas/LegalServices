package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.sql.SQLException;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;

@SpringBootApplication(scanBasePackages = {
	"com.github.karlnicholas.legalservices.opinionview.service", 
	"com.github.karlnicholas.legalservices.slipopinion.processor", 
	"com.github.karlnicholas.legalservices.opinionview.controller"
})
@EnableScheduling
@EnableAsync
public class SlipOpinionProcessor {
	Logger logger = LoggerFactory.getLogger(SlipOpinionProcessor.class);
	public static void main(String... args) {
		new SpringApplicationBuilder(SlipOpinionProcessor.class).run(args);
	}

	@Autowired TaskExecutor taskExecutor;
	@Autowired ObjectMapper objectMapper;
	@Autowired KakfaProperties kafkaProperties;
	@Autowired OpinionViewData opinionViewData;
	
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() throws SQLException {
		taskExecutor.execute(new OpinionViewCacheComponent(kafkaProperties, opinionViewData, integerOpinionViewConsumer()));

		taskExecutor.execute(new NewCaseListEntryProcessorComponent(objectMapper, kafkaProperties, integerJsonConsumer(), integerOpinionViewProducer()));
		taskExecutor.execute(new NewCaseListEntryProcessorComponent(objectMapper, kafkaProperties, integerJsonConsumer(), integerOpinionViewProducer()));
		taskExecutor.execute(new NewCaseListEntryProcessorComponent(objectMapper, kafkaProperties, integerJsonConsumer(), integerOpinionViewProducer()));

		taskExecutor.execute(new RetryCaseListEntryProcessorComponent(objectMapper, kafkaProperties, integerJsonConsumer(), integerOpinionViewProducer()));
		taskExecutor.execute(new RetryCaseListEntryProcessorComponent(objectMapper, kafkaProperties, integerJsonConsumer(), integerOpinionViewProducer()));
		taskExecutor.execute(new RetryCaseListEntryProcessorComponent(objectMapper, kafkaProperties, integerJsonConsumer(), integerOpinionViewProducer()));

		taskExecutor.execute(new CaseListProcessorComponent(objectMapper, kafkaProperties, integerJsonConsumer(), integerJsonProducer()));
	}

	@Bean
	public Producer<Integer, JsonNode> integerJsonProducer() {
        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerSerializer());
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getJsonValueSerializer());
        if ( !kafkaProperties.getUser().equalsIgnoreCase("notFound") ) {
            configProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            configProperties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            configProperties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
    		kafkaProperties.getUser() + "\" password=\"" + 
    		kafkaProperties.getPassword() + "\";");
        }
        
        return new KafkaProducer<>(configProperties);
	}

	@Bean 
	public Consumer<Integer, JsonNode> integerJsonConsumer() {
        //Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getJsonValueDeserializer());
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getSlipOpinionsConsumerGroup());
        if ( !kafkaProperties.getUser().equalsIgnoreCase("notFound") ) {
        	consumerProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        	consumerProperties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        	consumerProperties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
    		kafkaProperties.getUser() + "\" password=\"" + 
    		kafkaProperties.getPassword() + "\";");
        }

		// Create the consumer using props.
        return new KafkaConsumer<>(consumerProperties);
	}

	@Bean 
	public Producer<Integer, OpinionView> integerOpinionViewProducer() {
	    //Configure the Producer
	    Properties configProperties = new Properties();
	    configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
	    configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,kafkaProperties.getIntegerSerializer());
	    configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,kafkaProperties.getOpinionViewSerializer());
	    if ( !kafkaProperties.getUser().equalsIgnoreCase("notFound") ) {
	        configProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
	        configProperties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
	        configProperties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
			kafkaProperties.getUser() + "\" password=\"" + 
			kafkaProperties.getPassword() + "\";");
	    }
	    
	    return new KafkaProducer<>(configProperties);
	}

	@Bean 
	public Consumer<Integer, OpinionView> integerOpinionViewConsumer() {
	    //Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getOpinionViewDeserializer());
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getSlipOpinionsConsumerGroup());
	    if ( !kafkaProperties.getUser().equalsIgnoreCase("notFound") ) {
	    	consumerProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
	    	consumerProperties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
	    	consumerProperties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
			kafkaProperties.getUser() + "\" password=\"" + 
			kafkaProperties.getPassword() + "\";");
	    }
	
		// Create the consumer using props.
		 return new KafkaConsumer<>(consumerProperties);
	}
	//	@Bean
//	public WebMvcConfigurer configurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/**").allowedOrigins("http://localhost:3000");
//			}
//		};
//	}
}