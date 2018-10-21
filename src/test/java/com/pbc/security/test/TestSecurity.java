package com.pbc.security.test;

import static org.junit.Assert.assertEquals;

import java.security.PublicKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pbc.security.Security;

public class TestSecurity {

	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {

	}

	/*
	 * Testing Conditon(s): Default
	 */
	@Test
	public void testIsSignatureValid() {
		System.out.println("Now Testing Method:isSignatureValid");

		final String data = "g2NzAvvqLK5tyN6etglohNOEnlB32aQHeFqFgbSy7LQ=|$$|media_vault|$$|mgJS12VDsDFjTPQrALG4ec38ajExZCv4mC|$$|0450863AD64A87AE8A2FE83C1AF1A8403CB53F53E486D8511DAD8A04887E5B23522CD470243453A299FA9E77237716103ABC11A1DF38855ED6F2EE187E9C582BA6";

		final String signature = "MEUCIQCUi6OfObdrvoh2ElRuNhoBemiUP+QmHcWUXrM5+Ne9EQIgEUf7vgBVtu5SRTdnjIvIluToYR4OzrCfdokttYAFMNY=";
		final String pbcKey = "0450863AD64A87AE8A2FE83C1AF1A8403CB53F53E486D8511DAD8A04887E5B23522CD470243453A299FA9E77237716103ABC11A1DF38855ED6F2EE187E9C582BA6";

		// Constructor
		final Security instance = new Security();

		// Get expected result and result
		final boolean expResult = true;
		final boolean result = instance.isSignatureValid(pbcKey, signature, data);

		// Check Return value
		assertEquals(expResult, result);

	}

	// /*
	// * Testing Conditon(s): Default
	// */
	@Test
	public void testGetBTCPublicKey() throws Exception {
		System.out.println("Now Testing Method:getBTCPublicKey");
		final String pubKey = "0450863AD64A87AE8A2FE83C1AF1A8403CB53F53E486D8511DAD8A04887E5B23522CD470243453A299FA9E77237716103ABC11A1DF38855ED6F2EE187E9C582BA6";
		// Constructor
		final Security instance = new Security();

		// Get expected result and result
		final String expResult = "EC Public Key\n"
				+ "            X: 50863ad64a87ae8a2fe83c1af1a8403cb53f53e486d8511dad8a04887e5b2352\n"
				+ "            Y: 2cd470243453a299fa9e77237716103abc11a1df38855ed6f2ee187e9c582ba6";
		final PublicKey result = instance.getBTCPublicKey(pubKey);
		final String a = expResult.trim();
		System.out.println(a);
		final String b = result.toString().trim();
		System.out.println(b);
		// Check Return value
		assertEquals(a, b);

	}

}
