package com.github.karlnicholas.legalservices.opinionview.dao;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpinionViewRecord implements Comparable<OpinionViewRecord> {
	private LocalDate opinionDate;
	private byte[] opinionViewBytes;
	@Override
	public int compareTo(OpinionViewRecord o) {
		return opinionDate.compareTo(o.opinionDate);
	}
}
