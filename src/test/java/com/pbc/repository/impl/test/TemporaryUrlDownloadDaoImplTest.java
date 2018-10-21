package com.pbc.repository.impl.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.pbc.repository.impl.TemporaryUrlDownloadDaoImpl;
import com.pbc.repository.model.TemporaryUrlDownload;

@ContextConfiguration(locations = "classpath*:pbc-service-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class TemporaryUrlDownloadDaoImplTest {
	@Autowired
	private TemporaryUrlDownloadDaoImpl daoImpl;
	private final String uuid = "1234567890";
	private final String seconduuid = "0987654321";
	private final String filePath = "/home/administrator/Desktop/write.txt";
	private final boolean status = true;
	private final String dataHash = "qwertyuioplkjhgfdsazxcvbnm";

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void insertTest() {
		final TemporaryUrlDownload urlDownload = new TemporaryUrlDownload(uuid, filePath, status, dataHash);
		daoImpl.insert(urlDownload);
		final TemporaryUrlDownload path = daoImpl.getFilePath(uuid);
		Assert.assertEquals(filePath, path.getFilePath());
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void updateStatusTest() {
		final TemporaryUrlDownload urlDownload = new TemporaryUrlDownload(uuid, filePath, status, dataHash);
		daoImpl.insert(urlDownload);
		daoImpl.updateStatus(uuid, false);
		final TemporaryUrlDownload path = daoImpl.getFilePath(uuid);
		Assert.assertEquals(false, path.getStatus());
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void getFilePathTest() {
		final TemporaryUrlDownload urlDownload = new TemporaryUrlDownload(uuid, filePath, status, dataHash);
		daoImpl.insert(urlDownload);
		final TemporaryUrlDownload path = daoImpl.getFilePath(uuid);
		Assert.assertEquals(filePath, path.getFilePath());
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void bulkUrlInsertTest() {
		final TemporaryUrlDownload urlDownload = new TemporaryUrlDownload(uuid, filePath, status, dataHash);
		final TemporaryUrlDownload urlDownload2 = new TemporaryUrlDownload(seconduuid, filePath, status, dataHash);
		final List<TemporaryUrlDownload> list = new ArrayList<>();
		list.add(urlDownload);
		list.add(urlDownload2);
		daoImpl.bulkUrlInsert(list);
		final TemporaryUrlDownload filePath2 = daoImpl.getFilePath(seconduuid);
		final TemporaryUrlDownload filePath3 = daoImpl.getFilePath(uuid);
		Assert.assertEquals(seconduuid, filePath2.getUuid());
		Assert.assertEquals(uuid, filePath3.getUuid());
	}
}
