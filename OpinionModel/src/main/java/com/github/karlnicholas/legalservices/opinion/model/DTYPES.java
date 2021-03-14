package com.github.karlnicholas.legalservices.opinion.model;

public enum DTYPES {
	OPINIONBASE(0), SLIPOPINION(-1);

	private int dtype;
	DTYPES(int dtype) {
		this.dtype = dtype;
	}
	public int getDtype() {
		return dtype;
	}
}
