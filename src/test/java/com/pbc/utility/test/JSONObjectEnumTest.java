package com.pbc.utility.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.pbc.utility.JSONObjectEnum;

@ContextConfiguration(locations = "classpath*:pbc-service-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class JSONObjectEnumTest {

	@Test
	public void getByValueTest() {

		final JSONObjectEnum crc = JSONObjectEnum.CRC;
		Assert.assertEquals(JSONObjectEnum.CRC, crc.getByValue("crc"));
	}
}
