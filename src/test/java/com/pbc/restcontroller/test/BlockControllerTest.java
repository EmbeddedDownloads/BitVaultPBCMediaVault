package com.pbc.restcontroller.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbc.models.AcknowledgeRequest;
import com.pbc.models.GetMessageRequest;
import com.pbc.models.GetStatusRequest;
import com.pbc.repository.model.BlockStatus;
import com.pbc.restcontroller.BlockController;
import com.pbc.service.BlockService;
import com.pbc.service.TransactionMessageService;
import com.pbc.utility.IOFileUtil;

//@ContextConfiguration(locations = "classpath*:pbc-service-test-context.xml")
//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
public class BlockControllerTest {
	@InjectMocks
	private BlockController blockController;
	@Mock
	private BlockService blockService;
	@Mock
	private ObjectFactory<TransactionMessageService> transactionFactory;
	@Mock
	private ObjectFactory<IOFileUtil> ioUtils;

	private final String appId = "1234";
	private final String crc = "a268f384";
	private final String dataHash = "qwertyuiop";
	private final String filePath = System.getProperty("user.dir") + "/write.txt";
	private final String pbcId = "1111";
	private final String receiverAddress = "1212";
	private final String tag = "tag";
	private final long timeStamp = 122132222;
	private final String txnId = "123123";
	private final String serverKey = "1qazxswerdfgbhjk";

	private AcknowledgeRequest acknowledgeRequest;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		fileUploadStub();

	}

	private void fileUploadStub() {
		final IOFileUtil txnService = Mockito.mock(IOFileUtil.class);
		Mockito.when(ioUtils.getObject()).thenReturn(txnService);

		Mockito.when(txnService.getCorrectFolderPath()).thenReturn("hello");
		Mockito.when(txnService.getCompletePath("", "")).thenReturn(filePath);
	}

	@Test
	public void fileUploadTest() {
		try {
			final FileInputStream fileInputStream = new FileInputStream(filePath);
			final MockMultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);
			final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.blockController)
					.setViewResolvers(new StandaloneMvcTestViewResolver()).build();
			mockMvc.perform(MockMvcRequestBuilders.fileUpload("/uploadMedia").file(multipartFile).param("crc", crc)
					.param("id", txnId).param("walletAddress", receiverAddress).param("pbcId", pbcId)
					.param("timestamp", timeStamp + "").param("webServerKey", serverKey).param("tag", tag)
					.param("appId", appId)).andExpect(status().isOk());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getBlockStatusTest() throws Exception {
		final GetStatusRequest request = new GetStatusRequest();
		request.setTag("tag");
		request.setTransactionId("12345");
		final ObjectMapper mapper = new ObjectMapper();
		final String requestString = mapper.writeValueAsString(request);

		final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.blockController).build();
		mockMvc.perform(post("/block/mediaStatus").contentType(MediaType.APPLICATION_JSON).content(requestString))
				.andExpect(status().isOk());

	}

	@Test
	public void recieveBlockDataTest() throws Exception {

		final GetMessageRequest messageRequest = new GetMessageRequest();
		messageRequest.setId("1111");
		messageRequest.setTag("tag");
		messageRequest.setWalletAddress("192.168.11.200");
		messageRequest.setWalletPublicKey("1234567890987654321");
		messageRequest.setSignature("asdasdasdas");
		final ObjectMapper mapper = new ObjectMapper();
		final String req = mapper.writeValueAsString(messageRequest);
		final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.blockController).build();
		mockMvc.perform(post("/block/getMedia").contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isOk());

	}

	@Test
	public void acknowledgeDataTest() throws Exception {
		acknowledgeRequest = new AcknowledgeRequest();
		acknowledgeRequest.setCrc("8c035459");
		acknowledgeRequest.setTag("tag");
		acknowledgeRequest.setTransactionId("1234");
		final ObjectMapper mapper = new ObjectMapper();
		final String req = mapper.writeValueAsString(acknowledgeRequest);
		final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.blockController).build();
		mockMvc.perform(post("/block/deleteMedia").contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isOk());
	}

	@Test
	public void getFilePathTest() throws Exception {
		final String fileId = "23eh3urhb4932uhewcfuh";
		final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.blockController).build();
		mockMvc.perform(get("/block/getFile").param("fileId", fileId)).andExpect(status().isOk());
	}

	@Test
	public void getLogTest() {
		final String pointerLocation = "1234";
		final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.blockController).build();
		try {
			mockMvc.perform(get("/getLog").contentType(MediaType.TEXT_PLAIN).param("pointerLocation", pointerLocation))
					.andExpect(status().isOk());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getAllBlockDetailTest() {
		final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.blockController).build();
		try {
			Mockito.when(blockService.availableBlocks()).thenReturn((long) 5);
			Mockito.when(blockService.totalBlocks()).thenReturn((long) 5);
			Mockito.when(blockService.getDeletedBlockCount()).thenReturn((long) 5);
			final List<BlockStatus> values = new ArrayList<>();
			final BlockStatus blockStatus = new BlockStatus();
			values.add(blockStatus);
			Mockito.when(blockService.getBlockStatusListByPage(5)).thenReturn(values);
			mockMvc.perform(get("/getStatistics?pageNo=5").contentType(MediaType.TEXT_PLAIN))
					.andExpect(status().isOk());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getMapTest() {
		final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.blockController).build();
		try {
			mockMvc.perform(get("/getMap").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private class StandaloneMvcTestViewResolver extends InternalResourceViewResolver {
		public StandaloneMvcTestViewResolver() {
			super();
		}

		@Override
		protected AbstractUrlBasedView buildView(final String viewName) throws Exception {
			final InternalResourceView view = (InternalResourceView) super.buildView(viewName);
			view.setPreventDispatchLoop(false);
			return view;
		}
	}

}
