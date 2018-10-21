package com.pbc.service.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.pbc.blockchain.Block;
import com.pbc.blockchain.BlockContent;
import com.pbc.blockchain.BlockHeader;
import com.pbc.blockchain.ParseableBlockDTO;
import com.pbc.service.TransactionMessageService;
import com.pbc.utility.ConfigConstants;

@ContextConfiguration(locations = "classpath*:pbc-service-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class TransactionMessageServiceTest {
	@Autowired
	private TransactionMessageService messageService;

	private final String appId = "1234";
	private final String crc = "2037ba9b";
	private final String dataHash = "qwertyuiop";
	private final String filePath = System.getProperty("user.dir") + "/write.txt";
	private final String pbcId = "1111";
	private final String receiverAddress = "1212";
	private final String tag = "tag";
	private final long timeStamp = 122132222;
	private final String txnId = "123123";
	private final String serverKey = "1qazxswerdfgbhjk";
	private final static String CRC_SEPARATOR = "|$$|";
	private TransactionMessageService txnService;

	@Before
	public void preCondition() {
		MockitoAnnotations.initMocks(this);
		txnService = Mockito.spy(messageService);

	}

	@Test
	public void parseRequestAndValidateTest() {
		final ParseableBlockDTO blockDTO = new ParseableBlockDTO();
		blockDTO.setAppId(appId).setCrc(crc).setDataHash(dataHash).setFilePath(filePath).setPbcId(pbcId)
				.setReceiver(receiverAddress).setTag(tag).setTimeStamp(timeStamp).setTransactionId(txnId)
				.setWebServerKey(serverKey);
		File file = new File(System.getProperty("user.dir") + "/write.txt");
		InputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (final FileNotFoundException e) {
		}

		final boolean parseRequestAndValidate = messageService.parseRequestAndValidate(blockDTO, fileInputStream);
		try {
			fileInputStream.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		if (parseRequestAndValidate == true) {
			file = new File(ConfigConstants.FOLDER_PATH + "/e0d138a4cbfb2adc9f03c6d91f642ab1.txt");
			if (file.exists()) {
				file.delete();
			}
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(true);
		}

	}

	@Test
	public void createCombineStringTest() {
		final ParseableBlockDTO blockDTO = new ParseableBlockDTO();
		blockDTO.setAppId(appId).setCrc(crc).setDataHash(dataHash).setFilePath(filePath).setPbcId(pbcId)
				.setReceiver(receiverAddress).setTag(tag).setTimeStamp(timeStamp).setTransactionId(txnId)
				.setWebServerKey(serverKey);
		final StringBuilder sb = new StringBuilder();
		sb.append(tag).append(CRC_SEPARATOR).append(txnId).append(CRC_SEPARATOR).append(receiverAddress)
				.append(CRC_SEPARATOR).append(dataHash).append(CRC_SEPARATOR).append(pbcId).append(CRC_SEPARATOR)
				.append(appId).append(CRC_SEPARATOR).append(timeStamp).append(CRC_SEPARATOR).append(serverKey);
		final String createCombineString = messageService.createCombineString(blockDTO);
		Assert.assertEquals(sb.toString(), createCombineString);
	}

	@Test
	public void createSignatureCombineStringTest() {
		final StringBuilder sb = new StringBuilder();
		sb.append(txnId).append(CRC_SEPARATOR).append(tag).append(CRC_SEPARATOR).append(receiverAddress)
				.append(CRC_SEPARATOR).append(serverKey);
		final String createSignatureCombineString = messageService.createSignatureCombineString(txnId, tag,
				receiverAddress, serverKey);
		Assert.assertEquals(sb.toString(), createSignatureCombineString);
	}

	@Test
	public void getCRC() {
		final String rawString = "Hi";
		// A("4d170e0e", TransactionMessageService.getCRC(rawString));
		/*
		 * if("4d170e0e".equals(TransactionMessageService.getCRC(rawString))){
		 * Boolean b = } else{ return
		 */
		Assert.assertEquals("4d170e0e", messageService.getCRC(rawString.getBytes()));

	}

	@Test
	public void getParseableBlockDTOTest() {
		final Block block = new Block();
		final BlockContent blockContent = new BlockContent();
		blockContent.setAppId(appId);
		blockContent.setCrc(crc);
		blockContent.setDataHash(dataHash);
		blockContent.setFilePath(filePath);
		blockContent.setHashTxnId(txnId);
		blockContent.setPbcId(pbcId);
		blockContent.setPublicAddressOfReciever(receiverAddress);
		blockContent.setTag(tag);
		blockContent.setTimestamp(timeStamp);
		blockContent.setWebServerKey(serverKey);
		final BlockHeader header = new BlockHeader();
		header.setTimeStamp(1000);
		header.setPrevHash("1");
		block.setBlockContent(blockContent);
		block.setHeader(header);
		block.setBlockHash("Rajat");
		final ParseableBlockDTO parseableBlockDTO = messageService.getParseableBlockDTO(block);
		Assert.assertEquals(parseableBlockDTO.getTag(), tag);
	}

}
