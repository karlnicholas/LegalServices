package com.github.karlnicholas.legalservices.opinionview.kafka;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.kafka.common.serialization.Deserializer;

public class OpinionViewMessageDeserializer implements Deserializer<OpinionViewMessage> {

	@Override
	public OpinionViewMessage deserialize(String topic, byte[] data) {
		try {
		    ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(data));
		    OpinionViewMessage obj = (OpinionViewMessage) objIn.readObject();
			objIn.close();
		    return obj;
		} catch (IOException | ClassNotFoundException e) {
//			throw new RuntimeException(e);
			return null;
		}
	}
}