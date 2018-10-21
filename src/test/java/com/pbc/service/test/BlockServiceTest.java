package com.pbc.service.test;

import static org.mockito.Mockito.verify;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.pbc.blockchain.Block;
import com.pbc.blockchain.BlockResponseDTO;
import com.pbc.blockchain.ParseableBlockDTO;
import com.pbc.blockchain.creation.Persistor;
import com.pbc.models.CompleteRequest;
import com.pbc.repository.BlockStatusDao;
import com.pbc.repository.TemporaryUrlDownloadDao;
import com.pbc.repository.model.BlockStatus;
import com.pbc.repository.model.TemporaryUrlDownload;
import com.pbc.service.BlockService;

@ContextConfiguration(locations = "classpath*:pbc-service-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class BlockServiceTest {
	@InjectMocks
	private BlockService blockService;
	@Mock
	private BlockStatusDao blockStatusDao;
	@Mock
	private Persistor<Block> jsonPersistor;
	@Mock
	private TemporaryUrlDownloadDao downloadDao;

	private final String txnId = "1234567890";
	private final String tag = "secure_message";
	private final String dataHash = "d53c1bd6bdd0b1dbddb0fb14a3e1a5d2";
	private final String receiverAddress = "1212";
	private final String blockToBeDeletedStatus = "BLOCK_TO_BE_CREATED";
	BlockService service;

	@Before
	public void preCondition() {
		MockitoAnnotations.initMocks(this);
		createAndSaveBlockStub();
		getDownloadFileStub();
	}

	private void getDownloadFileStub() {
		final TemporaryUrlDownload value = new TemporaryUrlDownload("1234",
				System.getProperty("user.dir" + "/write.txt"), false, "asdfghjklqwertyuiop");
		Mockito.when(downloadDao.getFilePath("")).thenReturn(value);
	}

	@Test
	public void createAndSaveBlockTest() {
		final ParseableBlockDTO blockDTO = new ParseableBlockDTO();
		blockDTO.setAppId("1234").setCrc("crc").setDataHash(dataHash)
				.setFilePath(System.getProperty("user.dir") + "/write.txt").setPbcId("1111")
				.setReceiver(receiverAddress).setTag(tag).setTimeStamp(System.currentTimeMillis())
				.setTransactionId(txnId).setWebServerKey("1qazxswerdfgbhjk");
		// Mockito.doCallRealMethod().when(service).createAndSaveBlock(blockDTO);
		// Mockito.when(service.createAndSaveBlock(blockDTO)).thenCallRealMethod();
		service.createAndSaveBlock(blockDTO);
		final ArgumentCaptor<Block> argumentCaptor = ArgumentCaptor.forClass(Block.class);
		verify(service).saveBlock(argumentCaptor.capture());
		Assert.assertEquals("1234567890", argumentCaptor.getValue().getBlockContent().getHashTxnId());
	}

	@Test
	public void getDownloadFileTest() {
		blockService.getDownloadFile("1234");
	}

	@Test
	public void createActualBlockTest() {
		final ParseableBlockDTO blockDTO = new ParseableBlockDTO();
		blockDTO.setAppId("1234").setCrc("crc").setDataHash("qwertyuiop")
				.setFilePath(System.getProperty("user.dir") + "/write.txt").setPbcId("1111").setReceiver("1212")
				.setTag("tag").setTimeStamp(System.currentTimeMillis()).setTransactionId("123123")
				.setWebServerKey("1qazxswerdfgbhjk");
		final Block createActualBlock = blockService.createActualBlock(blockDTO);
		Assert.assertEquals("123123", createActualBlock.getBlockContent().getHashTxnId());

	}

	@Test
	public void calculateHashTest() {
		final String calculateHash = blockService.calculateHash((tag + txnId).getBytes(), "MD5");
		Assert.assertEquals(calculateHash, dataHash);

	}

	@Test
	public void createCompleteRequestTest() {
		final ParseableBlockDTO blockDTO = new ParseableBlockDTO();
		blockDTO.setAppId("1234").setCrc("crc").setDataHash("qwertyuiop")
				.setFilePath(System.getProperty("user.dir") + "/write.txt").setPbcId("1111").setReceiver("1212")
				.setTag("tag").setTimeStamp(System.currentTimeMillis()).setTransactionId("123123")
				.setWebServerKey("1qazxswerdfgbhjk");
		final CompleteRequest createCompleteRequest = blockService.createCompleteRequest(blockDTO);
		Assert.assertEquals("crc", createCompleteRequest.getCrc());
	}

	@Test
	public void getBlockResponseDTOTest() {
		final ParseableBlockDTO blockDTO = new ParseableBlockDTO();
		blockDTO.setAppId("1234").setCrc("crc").setDataHash("qwertyuiop")
				.setFilePath(System.getProperty("user.dir") + "/write.txt").setPbcId("1111").setReceiver("1212")
				.setTag("tag").setTimeStamp(System.currentTimeMillis()).setTransactionId("123123")
				.setWebServerKey("1qazxswerdfgbhjk");
		final Block createActualBlock = blockService.createActualBlock(blockDTO);
		final BlockResponseDTO blockResponseDTO = blockService.getBlockResponseDTO(createActualBlock);
		Assert.assertEquals("123123", blockResponseDTO.getTransactionId());
	}

	@Test
	public void getErrorMsgTest() {
		final StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html>");
		builder.append("<html>");
		builder.append("<head>");
		builder.append("</head>");
		builder.append("<body>");
		builder.append("<h3>");
		builder.append("No Information");
		builder.append("<h3>");
		builder.append("</body>");
		builder.append("</html>");
		Assert.assertEquals(builder.toString(), blockService.getErrorMsg("No Information"));
	}

	private void createAndSaveBlockStub() {
		service = Mockito.spy(blockService);
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setReceiverAddress(receiverAddress).setStatus(blockToBeDeletedStatus).setTag(tag)
				.setTransactionId(txnId).setCreatedAt(new Date()).setUpdatedAt(new Date());
		Mockito.when(service.getBlockStatus(tag, txnId)).thenReturn(blockStatus);
		final Block block = new Block();
		Mockito.doNothing().when(jsonPersistor).addBlock(block);
		Mockito.doNothing().when(blockStatusDao).updateStatus("", "", "", "");
	}
}
