package com.github.karlnicholas.opinionservices.slipopinion.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import opca.view.OpinionView;

@Component
public class OpinionViewCache {
	private final List<OpinionView> opinionViewCache;
	public OpinionViewCache() {
		opinionViewCache = new ArrayList<>();
	}
	public List<OpinionView> getCache() {
		return opinionViewCache;
	}
	public synchronized void addCache(OpinionView opinionView) {
		opinionViewCache.add(opinionView);
	}
}
