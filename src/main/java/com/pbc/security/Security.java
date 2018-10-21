package com.pbc.security;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Security {

	private final static Logger logger = Logger.getLogger(Security.class);

	private final String ECurve = "secp256k1";

	static {
		java.security.Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Verify the Signature of provided data.
	 *
	 * @param publicKey
	 * @param signature
	 * @param data
	 * @return
	 */
	public boolean isSignatureValid(final String publicKey, final String signature, final String data) {
		try {
			final PublicKey pubKey = getBTCPublicKey(publicKey);
			logger.info("Calculated publicKey : " + pubKey);
			final Signature sign = Signature.getInstance("ECDSA", "BC");
			sign.initVerify(pubKey);
			sign.update(data.getBytes());
			final boolean verifiedResult = sign.verify(Base64.getDecoder().decode(signature));
			logger.info("Signature verification result : " + verifiedResult);
			return verifiedResult;
		} catch (final Exception e) {
			logger.error("Problem while calculting signature ", e);
			return false;
		}
	}

	/* Get EC Public Key from BTC public key */
	public PublicKey getBTCPublicKey(final String pubKeyHex) throws Exception {

		final byte[] keyRaw = Hex.decode(pubKeyHex);
		final BigInteger xInt = new BigInteger(1, Arrays.copyOfRange(keyRaw, 1, 33));
		final BigInteger yInt = new BigInteger(1, Arrays.copyOfRange(keyRaw, 33, 65));

		final ECPoint pubPoint = new ECPoint(xInt, yInt);
		final AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", "BC");
		parameters.init(new ECGenParameterSpec(ECurve));
		final ECParameterSpec ecParameters = parameters.getParameterSpec(ECParameterSpec.class);

		final ECPublicKeySpec pubSpec = new ECPublicKeySpec(pubPoint, ecParameters);
		final KeyFactory kf = KeyFactory.getInstance("EC", "BC");
		final PublicKey key = kf.generatePublic(pubSpec);

		return key;
	}

}
