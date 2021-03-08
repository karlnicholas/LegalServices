package com.github.karlnicholas.opinionservices.slipopinion.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import opca.view.OpinionView;

public class OpinionViewSerializer {
	public byte[] serialize(OpinionView opinionView) {
		byte[] retVal = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(opinionView);
			retVal = baos.toByteArray();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retVal;
	}

}
