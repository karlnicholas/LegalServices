package com.github.karlnicholas.legalservices.opinionview.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.kafka.common.serialization.Serializer;

public class OpinionViewMessageSerializer implements Serializer<OpinionViewMessage> {
	@Override
	public byte[] serialize(String topic, OpinionViewMessage data) {
		byte[] retVal = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(data);
			retVal = baos.toByteArray();
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return retVal;
	}
}
