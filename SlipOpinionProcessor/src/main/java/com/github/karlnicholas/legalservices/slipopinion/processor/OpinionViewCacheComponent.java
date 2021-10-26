package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.github.karlnicholas.legalservices.opinionview.kafka.KakfaProperties;
import com.github.karlnicholas.legalservices.opinionview.kafka.OpinionViewData;
import com.github.karlnicholas.legalservices.opinionview.kafka.OpinionViewMessage;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;

public class OpinionViewCacheComponent implements Runnable {
	private final Logger log = LoggerFactory.getLogger(OpinionViewCacheComponent.class);
	private final Consumer<Integer, OpinionViewMessage> opinionViewCacheConsumer;
	private final KakfaProperties kafkaProperties;
	private final OpinionViewData opinionViewData;

	public OpinionViewCacheComponent(
			KakfaProperties kafkaProperties,
			OpinionViewData opinionViewData
	) {
		this.kafkaProperties = kafkaProperties;
		this.opinionViewData = opinionViewData;

		//Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getOpinionViewMessageDeserializer());
//		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getOpinionViewCacheConsumerGroup());
//		consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
        if ( !kafkaProperties.getUser().equalsIgnoreCase("notFound") ) {
        	consumerProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        	consumerProperties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        	consumerProperties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
    		kafkaProperties.getUser() + "\" password=\"" + 
    		kafkaProperties.getPassword() + "\";");
        }

		// Create the consumer using props.
        opinionViewCacheConsumer = new KafkaConsumer<>(consumerProperties);

	}

	@Override
    public void run(){
		try {
			// Subscribe to the topics.
//			opinionViewCacheConsumer.subscribe(Collections.singletonList(kafkaProperties.getOpinionViewCacheTopic()));
//			opinionViewCacheConsumer.poll(Duration.ZERO);  // without this, the assignment will be empty. 
//			opinionViewCacheConsumer.assignment().forEach(t -> {
//		        System.out.printf("Set %s to offset 0%n", t.toString());
//		        opinionViewCacheConsumer.seek(t, 0);
//		    });
			TopicPartition topicPartition = new TopicPartition(kafkaProperties.getOpinionViewCacheTopic(), 0);
			List<TopicPartition> partitions = Arrays.asList(topicPartition); 
			opinionViewCacheConsumer.assign(partitions);
			opinionViewCacheConsumer.seekToBeginning(partitions);
		    while (true) {
		        ConsumerRecords<Integer, OpinionViewMessage> opinionViewMessageRecords = opinionViewCacheConsumer.poll(Duration.ofSeconds(1));
		        for (ConsumerRecord<Integer, OpinionViewMessage> opinionViewMessageRecord : opinionViewMessageRecords) {
//		        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
//		        			opinionViewMessageRecord.topic(), opinionViewMessageRecord.partition(), opinionViewMessageRecord.offset());
	        		OpinionViewMessage opinionViewMessage = opinionViewMessageRecord.value();
	        		if ( opinionViewMessage.getOpinionView().isPresent() ) {
			        	opinionViewData.addOpinionView(opinionViewMessage.getOpinionView().get());
	        		}
	        		if ( opinionViewMessage.getCaseListEntry().isPresent() ) {
	        			CaseListEntry caseListEntry = opinionViewMessage.getCaseListEntry().get();
	        			SlipOpinion slipOpinion = new SlipOpinion(caseListEntry.getFileName(), caseListEntry.getFileExtension(), caseListEntry.getTitle(), caseListEntry.getOpinionDate(), caseListEntry.getCourt(), caseListEntry.getSearchUrl());
			        	opinionViewData.deleteOpinionView(slipOpinion.getOpinionKey());
	        		}
		        }
		    }
		} catch (WakeupException e) {
			log.error("WakeupException: {}", e);
		} catch (Exception e) {
			log.error("Unexpected error: {}", e);
		} finally {
			opinionViewCacheConsumer.close();
		}
	}
}

/*
public class KafkaRecordReader {

static final Map<String, Object> props = new HashMap<>();
static {
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
    props.put(ConsumerConfig.CLIENT_ID_CONFIG, "sample-client");
}

public static void main(String[] args) {
    final Map<TopicPartition, OffsetInfo> partitionOffsetInfos = getOffsets(Arrays.asList("world, sample"));
    final List<ConsumerRecord<byte[], byte[]>> records = readRecords(partitionOffsetInfos);

    System.out.println(partitionOffsetInfos);
    System.out.println("Read : " + records.size() + " records");
}

private static List<ConsumerRecord<byte[], byte[]>> readRecords(final Map<TopicPartition, OffsetInfo> offsetInfos) {
    final Properties readerProps = new Properties();
    readerProps.putAll(props);
    readerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, "record-reader");

    final Map<TopicPartition, Boolean> partitionToReadStatusMap = new HashMap<>();
    offsetInfos.forEach((tp, offsetInfo) -> {
        partitionToReadStatusMap.put(tp, offsetInfo.beginOffset == offsetInfo.endOffset);
    });

    final List<ConsumerRecord<byte[], byte[]>> cachedRecords = new ArrayList<>();
    try (final KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(readerProps)) {
        consumer.assign(offsetInfos.keySet());
        for (final Map.Entry<TopicPartition, OffsetInfo> entry : offsetInfos.entrySet()) {
            consumer.seek(entry.getKey(), entry.getValue().beginOffset);
        }

        boolean close = false;
        while (!close) {
            final ConsumerRecords<byte[], byte[]> consumerRecords = consumer.poll(Duration.ofMillis(100));
            for (final ConsumerRecord<byte[], byte[]> record : consumerRecords) {
                cachedRecords.add(record);
                final TopicPartition currentTp = new TopicPartition(record.topic(), record.partition());
                if (record.offset() + 1 == offsetInfos.get(currentTp).endOffset) {
                    partitionToReadStatusMap.put(currentTp, true);
                }
            }

            boolean done = true;
            for (final Map.Entry<TopicPartition, Boolean> entry : partitionToReadStatusMap.entrySet()) {
                done &= entry.getValue();
            }
            close = done;
        }
    }
    return cachedRecords;
}

private static Map<TopicPartition, OffsetInfo> getOffsets(final List<String> topics) {
    final Properties offsetReaderProps = new Properties();
    offsetReaderProps.putAll(props);
    offsetReaderProps.put(ConsumerConfig.CLIENT_ID_CONFIG, "offset-reader");

    final Map<TopicPartition, OffsetInfo> partitionOffsetInfo = new HashMap<>();
    try (final KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(offsetReaderProps)) {
        final List<PartitionInfo> partitionInfos = new ArrayList<>();
        topics.forEach(topic -> partitionInfos.addAll(consumer.partitionsFor("sample")));
        final Set<TopicPartition> topicPartitions = partitionInfos
                .stream()
                .map(x -> new TopicPartition(x.topic(), x.partition()))
                .collect(Collectors.toSet());
        consumer.assign(topicPartitions);
        final Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(topicPartitions);
        final Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);

        for (final TopicPartition tp : topicPartitions) {
            partitionOffsetInfo.put(tp, new OffsetInfo(beginningOffsets.get(tp), endOffsets.get(tp)));
        }
    }
    return partitionOffsetInfo;
}

private static class OffsetInfo {

    private final long beginOffset;
    private final long endOffset;

    private OffsetInfo(long beginOffset, long endOffset) {
        this.beginOffset = beginOffset;
        this.endOffset = endOffset;
    }

    @Override
    public String toString() {
        return "OffsetInfo{" +
                "beginOffset=" + beginOffset +
                ", endOffset=" + endOffset +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffsetInfo that = (OffsetInfo) o;
        return beginOffset == that.beginOffset &&
                endOffset == that.endOffset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginOffset, endOffset);
    }
}
}
*/