package com.pommert.jedidiah.bouncecraft.fmp;

import codechicken.multipart.MultiPartRegistry;

import com.pommert.jedidiah.bouncecraft.fmp.factory.BCPartFactory;

public class BCFMP {
	public static void init() {
		MultiPartRegistry.registerParts(new BCPartHandler(),
				new String[] { "bc_multipart" });

		BCPartHandler.addPartFactory("bc_multipart", new BCPartFactory());
	}
}