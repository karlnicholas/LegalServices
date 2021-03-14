package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.kafka.common.serialization.Serializer;

import com.github.karlnicholas.legalservices.opinionview.view.OpinionView;

public class OpinionViewSerializer implements Serializer<OpinionView> {
	@Override
	public byte[] serialize(String topic, OpinionView data) {
		byte[] retVal = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(data);
			retVal = baos.toByteArray();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
