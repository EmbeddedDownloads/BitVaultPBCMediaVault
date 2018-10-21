package com.pbc.service.test;

import org.junit.Assert;

import com.pbc.models.BlockStatusEnum;
import com.pbc.models.CustomResponse;
import com.pbc.service.UtilityService;

public class UtilityServiceTest {

	@org.junit.Test
	public void getCustomResponseEnumTest() {
		final CustomResponse<Object> response = new CustomResponse<>("Success");
		Assert.assertEquals(UtilityService.getCustomResponseEnum(response, BlockStatusEnum.SAVED), response);
	}
}