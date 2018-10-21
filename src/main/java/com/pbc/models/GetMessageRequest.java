package com.pbc.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GetMessageRequest {

	private String tag;
	private String id;
	private String walletAddress;
	private String walletPublicKey;
	private String signature;

	public String getTag() {
		return tag;
	}

	public void setTag(final String tag) {
		this.tag = tag;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(final String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public String getWalletPublicKey() {
		return walletPublicKey;
	}

	public void setWalletPublicKey(final String walletPublicKey) {
		this.walletPublicKey = walletPublicKey;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(final String signature) {
		this.signature = signature;
	}

}
