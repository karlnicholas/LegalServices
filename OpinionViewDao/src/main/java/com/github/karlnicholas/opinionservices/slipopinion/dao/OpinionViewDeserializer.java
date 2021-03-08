package com.github.karlnicholas.opinionservices.slipopinion.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import opca.view.OpinionView;

public class OpinionViewDeserializer {

	public OpinionView deserialize(byte[] data) {
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