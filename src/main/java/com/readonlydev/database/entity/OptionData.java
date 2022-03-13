package com.readonlydev.database.entity;

import com.readonlydev.util.entity.ReferenceData;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OptionData {
	String addonLinksChannelId;
	
	public void setReferencedMessageChannel(ReferenceData referenceData) {
		referenceData.setAddonChannelId(addonLinksChannelId);
	}
}
