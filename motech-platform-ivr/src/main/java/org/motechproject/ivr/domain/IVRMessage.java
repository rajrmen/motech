package org.motechproject.ivr.domain;

import org.springframework.stereotype.Component;

@Component
public interface IVRMessage {

	public String getText(String key);

	public String getWav(String key, String preferredLangCode);
}