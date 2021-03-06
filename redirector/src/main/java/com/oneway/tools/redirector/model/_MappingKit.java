package com.oneway.tools.redirector.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = base ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("applyinfo", "id", Applyinfo.class);
		arp.addMapping("partylist", "id", Partylist.class);
		arp.addMapping("report", "id", Report.class);
		arp.addMapping("userlist", "id", Userlist.class);
	}
}

