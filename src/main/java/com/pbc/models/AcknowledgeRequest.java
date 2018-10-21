package com.pbc.models;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class AcknowledgeRequest {

	@NotNull
	@JsonProperty(value = "id")
	private String transactionId;

	private String crc;
	private String tag;
	private String walletAddress;
	private String walletPublicKey;
	private String signature;

	public String getTransactionId() {
		return transactionId;
	}

	public AcknowledgeRequest setTransactionId(final String transactionId) {
		this.transactionId = transactionId;
		return this;
	}

	public String getCrc() {
		return crc;
	}

	public AcknowledgeRequest setCrc(final String crc) {
		this.crc = crc;
		return this;
	}

	public String getTag() {
		return tag;
	}

	public AcknowledgeRequest setTag(final String tag) {
		this.tag = tag;
		return this;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public AcknowledgeRequest setWalletAddress(final String walletAddress) {
		this.walletAddress = walletAddress;
		return this;
	}

	public String getWalletPublicKey() {
		return walletPublicKey;
	}

	public AcknowledgeRequest setWalletPublicKey(final String walletPublicKey) {
		this.walletPublicKey = walletPublicKey;
		return this;
	}

	public String getSignature() {
		return signature;
	}

	public AcknowledgeRequest setSignature(final String signature) {
		this.signature = signature;
		return this;
	}

	@Override
	public String toString() {
		return "AcknowledgeRequest [transactionId=" + transactionId + ", crc=" + crc + ", tag=" + tag
				+ ", walletAddress=" + walletAddress + ", walletPublicKey=" + walletPublicKey + ", signature="
				+ signature + "]";
	}

}
