package com.pbc.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CrcConfirmMapModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -1475157835106554286L;
	private Map<String, List<ConfirmationHelper>> confirmationMap;
	private Map<String, List<String>> crcMap;

	public Map<String, List<ConfirmationHelper>> getConfirmationMap() {
		return confirmationMap;
	}

	public void setConfirmationMap(final Map<String, List<ConfirmationHelper>> confirmationMap) {
		this.confirmationMap = confirmationMap;
	}

	public Map<String, List<String>> getCrcMap() {
		return crcMap;
	}

	public void setCrcMap(final Map<String, List<String>> crcMap) {
		this.crcMap = crcMap;
	}
}
