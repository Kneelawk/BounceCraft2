package com.pommert.jedidiah.bouncecraft2.fmp;

import codechicken.multipart.MultiPartRegistry;

import com.pommert.jedidiah.bouncecraft2.fmp.factory.BCPartFactory;

public class BCFMP {
	public static void init() {
		MultiPartRegistry.registerParts(new BCPartHandler(),
				new String[] { "bc_multipart" });

		BCPartHandler.addPartFactory("bc_multipart", new BCPartFactory());
	}
}