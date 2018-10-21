package com.pbc.utility.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.pbc.utility.ConfigConstants;
import com.pbc.utility.IOFileUtil;

@ContextConfiguration(locations = "classpath*:pbc-service-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class IOFileUtilTest {

	@Autowired
	private IOFileUtil fileUtil;

	private final String txnId = "1234567890";
	private final String tag = "secure_message";

	@Test
	public void writObjectLocallyTest() {
		final File file = new File(System.getProperty("user.dir") + "/write.txt");
		try (final FileInputStream fileInputStream = new FileInputStream(file);) {
			fileUtil.writObjectLocally(fileInputStream, tag + txnId);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		final File fileCreated = new File(ConfigConstants.FOLDER_PATH + "/d53c1bd6bdd0b1dbddb0fb14a3e1a5d2.txt");
		final boolean exists = fileCreated.exists();
		if (exists == true) {
			fileCreated.delete();
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(false);
		}
	}
}
