package com.pommert.jedidiah.bouncecraft2.fmp;

import java.util.TreeMap;

import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.TMultiPart;

import com.pommert.jedidiah.bouncecraft2.fmp.factory.IBCPartFactory;

public class BCPartHandler implements IPartFactory {
	public static final TreeMap<String, IBCPartFactory> factories = new TreeMap<String, IBCPartFactory>();

	@Override
	public TMultiPart createPart(String name, boolean client) {
		IBCPartFactory factory = factories.get(name);
		if (factory == null)
			return null;
		return factory.createPart(name, client);
	}

	public static void addPartFactory(String name, IBCPartFactory factory) {
		factories.put(name, factory);
	}
}
