package com.pommert.jedidiah.bouncecraft2.fmp.factory;

import codechicken.multipart.TMultiPart;

public interface IBCPartFactory {
	public abstract TMultiPart createPart(String name, boolean client);
}
