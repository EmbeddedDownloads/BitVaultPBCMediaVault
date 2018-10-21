package com.pbc.repository.impl.test;

import java.util.Arrays;
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

import com.pbc.repository.impl.BlockStatusDaoImpl;
import com.pbc.repository.model.BlockStatus;

@ContextConfiguration(locations = "classpath*:pbc-service-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

public class BlockStatusDaoImplTest {

	@Autowired
	private BlockStatusDaoImpl blockStatusDao;

	// @Before
	// public void preCondition() {
	// LogLog.setQuietMode(true);
	// }

	private final String transactionId = "1";
	private final String updateTransactionId = "2";
	private final String tag = "secure_message";
	private final String receiverAddress = "1212";
	private final String status = "SAVED";
	private final String blockToBeDeletedStatus = "BLOCK_TO_BE_CREATED";
	private final String deletedStatus = "DELETED";

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void insertTest() {
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setTransactionId(transactionId).setTag(tag).setReceiverAddress(receiverAddress).setStatus(status);
		blockStatusDao.insert(blockStatus);
		final BlockStatus blockStatusRetrived = blockStatusDao.getStatus(tag, transactionId);
		Assert.assertEquals(status, blockStatusRetrived.getStatus());
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void updateStatusTest() {
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setTransactionId(transactionId).setTag(tag).setReceiverAddress(receiverAddress).setStatus(status);
		blockStatusDao.insert(blockStatus);
		blockStatusDao.updateStatus(tag, transactionId, status, receiverAddress);
		final BlockStatus blockStatusReteived = blockStatusDao.getStatus(tag, transactionId);
		Assert.assertEquals(blockStatusReteived.getStatus(), status);
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void getStatusTest() {
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setTransactionId(transactionId).setTag(tag).setReceiverAddress(receiverAddress).setStatus(status);
		blockStatusDao.insert(blockStatus);
		final BlockStatus blockStatusRetrived = blockStatusDao.getStatus(tag, transactionId);
		Assert.assertEquals(status, blockStatusRetrived.getStatus());
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void getStatusArrayTest() {
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setTransactionId(transactionId).setTag(tag).setReceiverAddress(receiverAddress).setStatus(status);
		blockStatusDao.insert(blockStatus);
		blockStatus.setTransactionId(updateTransactionId).setTag(tag).setStatus(status)
				.setReceiverAddress(receiverAddress);
		blockStatusDao.insert(blockStatus);
		final String[] txnArray = { transactionId, updateTransactionId };
		final String[] tagArray = { tag, tag };
		final List<BlockStatus> list = blockStatusDao.getStatus(tagArray, txnArray);
		Assert.assertEquals(2, list.size());
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void getBlockListTest() {
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setTransactionId(transactionId).setTag(tag).setReceiverAddress(receiverAddress).setStatus(status);
		blockStatusDao.insert(blockStatus);
		blockStatus.setTransactionId(updateTransactionId).setTag(tag).setStatus(status)
				.setReceiverAddress(receiverAddress);
		blockStatusDao.insert(blockStatus);
		final List<String> receiverAddressList = Arrays.asList(receiverAddress);

		final List<BlockStatus> blockList = blockStatusDao.getBlockList(receiverAddressList);
		Assert.assertEquals(2, blockList.size());
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void getTotalBlockCountTest() {
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setTransactionId(transactionId).setTag(tag).setReceiverAddress(receiverAddress).setStatus(status);
		blockStatusDao.insert(blockStatus);
		blockStatus.setTransactionId(updateTransactionId).setTag(tag).setStatus(status)
				.setReceiverAddress(receiverAddress);
		blockStatusDao.insert(blockStatus);
		final long totalBlockCount = blockStatusDao.getTotalBlockCount();
		Assert.assertEquals(2, totalBlockCount);
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void getAvailableBlockCountTest() {
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setTransactionId(transactionId).setTag(tag).setReceiverAddress(receiverAddress).setStatus(status);
		blockStatusDao.insert(blockStatus);
		blockStatus.setTransactionId(updateTransactionId).setTag(tag).setStatus(status)
				.setReceiverAddress(receiverAddress);
		blockStatusDao.insert(blockStatus);
		final long availableBlockCount = blockStatusDao.getAvailableBlockCount();
		Assert.assertEquals(2, availableBlockCount);
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void getDeletedBlockCountTest() {
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setTransactionId(transactionId).setTag(tag).setReceiverAddress(receiverAddress).setStatus(status);
		blockStatusDao.insert(blockStatus);
		blockStatus.setTransactionId(updateTransactionId).setTag(tag).setStatus(deletedStatus)
				.setReceiverAddress(receiverAddress);
		blockStatusDao.insert(blockStatus);
		final long deletedBlockCount = blockStatusDao.getDeletedBlockCount();
		Assert.assertEquals(1, deletedBlockCount);
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void getBlockToBeCreatedListTest() {
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setTransactionId(transactionId).setTag(tag).setReceiverAddress(receiverAddress)
				.setStatus(blockToBeDeletedStatus);
		blockStatusDao.insert(blockStatus);
		blockStatus.setTransactionId(updateTransactionId).setTag(tag).setStatus(blockToBeDeletedStatus)
				.setReceiverAddress(receiverAddress);
		blockStatusDao.insert(blockStatus);
		final List<BlockStatus> blockToBeCreatedList = blockStatusDao.getBlockToBeCreatedList();
		Assert.assertEquals(2, blockToBeCreatedList.size());
	}

	@Test
	@Transactional(transactionManager = "txManager")
	@Rollback
	public void getStatusifSavedTest() {
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setTransactionId(transactionId).setTag(tag).setReceiverAddress(receiverAddress).setStatus(status);
		blockStatusDao.insert(blockStatus);
		final BlockStatus statusifSaved = blockStatusDao.getStatusifSaved(tag, transactionId);
		Assert.assertEquals(status, statusifSaved.getStatus());
	}
}
