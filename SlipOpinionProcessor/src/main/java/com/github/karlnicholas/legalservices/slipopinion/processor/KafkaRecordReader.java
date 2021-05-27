package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

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
			final Set<TopicPartition> topicPartitions = partitionInfos.stream()
					.map(x -> new TopicPartition(x.topic(), x.partition())).collect(Collectors.toSet());
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
			return "OffsetInfo{" + "beginOffset=" + beginOffset + ", endOffset=" + endOffset + '}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			OffsetInfo that = (OffsetInfo) o;
			return beginOffset == that.beginOffset && endOffset == that.endOffset;
		}

		@Override
		public int hashCode() {
			return Objects.hash(beginOffset, endOffset);
		}
	}
}
