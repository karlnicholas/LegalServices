package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.kafka.common.serialization.Deserializer;

import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;

public class OpinionViewDeserializer implements Deserializer<OpinionView> {

	@Override
	public OpinionView deserialize(String topic, byte[] data) {
		try {
		    ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(data));
			OpinionView obj = (OpinionView) objIn.readObject();
			objIn.close();
		    return obj;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}