package com.readonlydev.util.entity;

import java.util.ArrayList;
import java.util.List;

import com.readonlydev.common.version.Version;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Addon {
	private AddonData addonData = new AddonData();
	private ReferenceData referenceData = new ReferenceData();
	
	@Data
	@NoArgsConstructor
	public static class AddonData {
		private String modName = "";
		private String iconUrl = "";
		private String desc = "";
		private Version recentVersion;
		private List<Version> MinecraftVersions = new ArrayList<>();
		private String downloadLink = "";
		private String sourceCodeLink = "";
		private String issueTrackerLink = "";
	}
}
