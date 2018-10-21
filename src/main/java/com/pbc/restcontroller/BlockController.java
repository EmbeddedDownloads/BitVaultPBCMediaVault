package com.pbc.restcontroller;

import static com.pbc.utility.CustomMessageConstants.BLOCK_ALREADY_DELETED;
import static com.pbc.utility.CustomMessageConstants.BLOCK_DELETED_SUCCESSFULLY;
import static com.pbc.utility.CustomMessageConstants.DELETED_ALRDY_RECEIVED;
import static com.pbc.utility.CustomMessageConstants.ERR_IN_CREATE_BLOCK;
import static com.pbc.utility.CustomMessageConstants.FILE_UPLOADED_SUCCESSFULLY;
import static com.pbc.utility.CustomMessageConstants.MSG_BLOCK_RETURN_SUCCESSFULLY;
import static com.pbc.utility.CustomMessageConstants.MSG_NOT_VALID;
import static com.pbc.utility.CustomMessageConstants.NO_MESSAGE_FOR_TXNID;
import static com.pbc.utility.CustomMessageConstants.SIGN_NOT_VALID;
import static com.pbc.utility.CustomMessageConstants.STR_IS;
import static com.pbc.utility.CustomMessageConstants.STR_STATUS_MESSAGE;

import java.io.File;
import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pbc.blockchain.Block;
import com.pbc.blockchain.BlockResponseDTO;
import com.pbc.blockchain.ParseableBlockDTO;
import com.pbc.models.AcknowledgeRequest;
import com.pbc.models.BlockStatusEnum;
import com.pbc.models.CrcConfirmMapModel;
import com.pbc.models.CustomErrorResponse;
import com.pbc.models.CustomResponse;
import com.pbc.models.CustomSuccessResponse;
import com.pbc.models.GetMessageRequest;
import com.pbc.models.GetStatusRequest;
import com.pbc.models.StatisticsModel;
import com.pbc.repository.model.BlockStatus;
import com.pbc.service.BlockService;
import com.pbc.service.TransactionMessageService;
import com.pbc.utility.CustomMessageConstants;
import com.pbc.utility.IOFileUtil;
import com.pbc.utility.StringConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "BlockController", description = "Controller for all the opertion.")
@RequestMapping("/")
public class BlockController {

	// URL constants
	private static final String UPLOAD_MEDIA_URL = "/uploadMedia";
	private static final String MEDIA_STATUS_URL = "/block/mediaStatus";
	private static final String GET_MEDIA_URL = "/block/getMedia";
	private static final String DELETE_MEDIA_URL = "/block/deleteMedia";
	private static final String GET_FILE = "/block/getFile";
	private static final String GET_LOG = "/getLog";
	private static final String GET_STATISTICS = "/getStatistics";
	private static final String GET_CRC_CONFIRM_MAP = "/getMap";

	private static final Logger logger = Logger.getLogger(BlockController.class);
	private static final Logger reportLogger = Logger.getLogger("reportsLogger");

	@Autowired
	private BlockService blockService;

	@Autowired
	private ObjectFactory<TransactionMessageService> transactionFactory;

	@Autowired
	private ObjectFactory<IOFileUtil> ioUtils;

	@ResponseBody
	@ApiOperation(value = "Api used to upload the file.")
	@RequestMapping(value = UPLOAD_MEDIA_URL, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomResponse<String> fileUpload(@NotNull @RequestParam("file") final MultipartFile file,
			@NotNull @RequestParam("id") final String id, @NotNull @RequestParam("tag") final String tag,
			@NotNull @RequestParam("crc") final String crc,
			@NotNull @RequestParam("walletAddress") final String walletAddress,
			@NotNull @RequestParam("pbcId") final String pbcId, @NotNull @RequestParam("appId") final String appId,
			@NotNull @RequestParam("timestamp") final String timestamp,
			@NotNull @RequestParam("webServerKey") final String webServerKey) {
		CustomResponse<String> customResponse = null;

		logger.info("Request received for transaction id : " + id);
		reportLogger.fatal("Request received for transaction id : " + id);
		final String correctFolderPath = ioUtils.getObject().getCorrectFolderPath();
		final String completePath = ioUtils.getObject().getCompletePath(tag + id, correctFolderPath);
		logger.info("Complete path: " + completePath);
		final File fileToWrite = new File(completePath);
		if (fileToWrite.exists()) {
			logger.warn("Transaction id already exist : " + id);
			reportLogger.fatal("Request already exists for transaction id : " + id);
			customResponse = new CustomErrorResponse<>();
			customResponse.setMessage(CustomMessageConstants.TXN_ALREADY_EXIST + " :: " + id);
			return customResponse;
		} else {
			try {
				fileToWrite.createNewFile();
			} catch (final IOException e) {
				logger.error("Unable to create a new File for: " + tag + id);
			}
		}
		final BlockStatus blockStatus = new BlockStatus();
		blockStatus.setTransactionId(id).setTag(tag).setReceiverAddress(walletAddress);
		try {
			final ParseableBlockDTO parseableBlockDTO = new ParseableBlockDTO();
			parseableBlockDTO.setTransactionId(id).setTag(tag).setCrc(crc).setAppId(appId).setPbcId(pbcId)
					.setReceiver(walletAddress).setTimeStamp(Long.parseLong(timestamp)).setWebServerKey(webServerKey);

			logger.info("Complete Request data:: " + parseableBlockDTO.toString());

			final boolean isValidMedia = transactionFactory.getObject().parseRequestAndValidate(parseableBlockDTO,
					file.getInputStream());
			if (!isValidMedia) {
				logger.warn("CRC sent does not match with calculated crc for Id : " + id);
				reportLogger.fatal("CRC is not valid for Id : " + id);
				customResponse = new CustomErrorResponse<>();
				customResponse.setMessage(MSG_NOT_VALID);
				final File fileToDelete = new File(parseableBlockDTO.getFilePath());
				if (fileToDelete.exists()) {
					fileToDelete.delete();
				}
				return customResponse;
			}
			reportLogger.fatal("CRC validated successfully for id : " + id);
			blockStatus.setStatus(BlockStatusEnum.INPROCESS.name());
			if (blockService.taskToNotify(blockStatus, parseableBlockDTO)) {
				customResponse = new CustomSuccessResponse<>();
				customResponse.setMessage(FILE_UPLOADED_SUCCESSFULLY);
				return customResponse;
			} else {
				logger.warn("Transaction id already exist : " + id);
				reportLogger.fatal("Request already exists for transaction id : " + id);
				customResponse = new CustomErrorResponse<>();
				customResponse.setMessage(CustomMessageConstants.TXN_ALREADY_EXIST + " :: " + id);
				return customResponse;
			}
		} catch (final Exception ex) {
			logger.error("An error occured while creating block for id : " + id, ex);
			blockStatus.setStatus(BlockStatusEnum.ERROR_OCCURED.name());
			blockService.insertOrUpdate(blockStatus);
			customResponse = new CustomErrorResponse<>();
			customResponse.setMessage(ERR_IN_CREATE_BLOCK);
			return customResponse;
		}
	}

	/**
	 * Method returns details from transaction received for given transaction
	 * id.
	 *
	 * @param completeRequest
	 * @return
	 */
	@ResponseBody
	@ApiOperation(value = "Api used to get status of a request.")
	@RequestMapping(value = MEDIA_STATUS_URL, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomResponse<String> getBlockStatus(@RequestBody @NotNull @Validated final GetStatusRequest request) {

		logger.info(
				"Block status get request received for combined key: " + request.getTag() + request.getTransactionId());
		CustomResponse<String> response = null;
		final BlockStatus blockStatus = blockService.getBlockStatus(request.getTag(), request.getTransactionId());
		if (null == blockStatus) {
			response = new CustomErrorResponse<>();
			response.setMessage(CustomMessageConstants.NO_MESSAGE);
			return response;
		}

		final String status = blockStatus.getStatus();
		response = new CustomSuccessResponse<>();

		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(STR_STATUS_MESSAGE);
		stringBuilder.append(StringConstants.SPACE);
		stringBuilder.append(request.getTransactionId());
		stringBuilder.append(StringConstants.SPACE);
		stringBuilder.append(STR_IS);
		stringBuilder.append(StringConstants.SPACE);
		stringBuilder.append(status);

		response.setMessage(stringBuilder.toString());
		((CustomSuccessResponse<String>) response).setResultSet(status);
		logger.info(stringBuilder.toString() + " GetMessage by txnid " + blockStatus.getTag()
				+ blockStatus.getTransactionId());
		return response;
	}

	/**
	 * Method returns whole block received for given transaction id.
	 *
	 * @param completeRequest
	 * @return
	 */
	@ResponseBody
	@ApiOperation(value = "Api used to get all information for a given request.")
	@RequestMapping(value = GET_MEDIA_URL, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Object recieveBlockData(@RequestBody @NotNull @Validated final GetMessageRequest getMessageRequest) {
		CustomResponse<BlockResponseDTO> response = null;
		final String combineKey = getMessageRequest.getTag() + getMessageRequest.getId();
		logger.info("Get message request for combined Key: " + combineKey);
		reportLogger.fatal("Get message requested for transaction id : " + getMessageRequest.getId());
		final Block block = blockService.getBlock(combineKey);
		if (block == null) {
			reportLogger.fatal(NO_MESSAGE_FOR_TXNID + getMessageRequest.getId());
			response = new CustomErrorResponse<>();
			response.setMessage(NO_MESSAGE_FOR_TXNID);
			return response;
		}
		final BlockResponseDTO blockResponseDTO = blockService.getBlockResponseDTO(block);

		final String combineData = transactionFactory.getObject().createSignatureCombineString(
				getMessageRequest.getId(), getMessageRequest.getTag(), getMessageRequest.getWalletAddress(),
				getMessageRequest.getWalletPublicKey());
		if (!blockService.isSignatureValid(getMessageRequest.getWalletPublicKey(), getMessageRequest.getSignature(),
				combineData)) {
			logger.info("Signature is not valid.");
			final CustomResponse<BlockResponseDTO> customResponse = new CustomErrorResponse<>();
			customResponse.setMessage("Signature is not valid.");
			return customResponse;
		}

		blockService.createDownloadUrl(blockResponseDTO, block);
		response = new CustomSuccessResponse<>();
		response.setMessage(MSG_BLOCK_RETURN_SUCCESSFULLY);
		((CustomSuccessResponse<BlockResponseDTO>) response).setResultSet(blockResponseDTO);
		logger.info(MSG_BLOCK_RETURN_SUCCESSFULLY + " for transaction id " + combineKey + " Block data "
				+ block.toString());
		reportLogger.fatal(MSG_BLOCK_RETURN_SUCCESSFULLY + " for transaction id " + getMessageRequest.getId());
		return response;
	}

	@ResponseBody
	@ApiOperation(value = "Api used to delete information for given request.")
	@RequestMapping(value = DELETE_MEDIA_URL, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomResponse<String> acknowledgeData(
			@RequestBody @NotNull @Validated final AcknowledgeRequest acknowledgeRequest) {
		final String combinedKey = acknowledgeRequest.getTag() + acknowledgeRequest.getTransactionId();
		logger.info("Acknowledge request for data with combined Key: " + combinedKey + " and crc: "
				+ acknowledgeRequest.getCrc());
		reportLogger
				.fatal("Data successfully received by receiver. Validating acknowledge to delete block from blockchain for transaction id "
						+ acknowledgeRequest.getTransactionId());
		CustomResponse<String> customResponse = null;
		final BlockStatus blockStatus = blockService.getBlockStatus(acknowledgeRequest.getTag(),
				acknowledgeRequest.getTransactionId());
		if (null == blockStatus) {
			reportLogger.fatal("No block found for transaction id : " + acknowledgeRequest.getTransactionId());
			logger.warn("Block was not found for given combined key: " + combinedKey);
			customResponse = new CustomErrorResponse<>();
			customResponse.setMessage(NO_MESSAGE_FOR_TXNID);
			return customResponse;
		}
		if (blockStatus.getStatus().equals(BlockStatusEnum.DELETED.name())) {
			logger.info("As block was already deleted so returning success response for combine key " + combinedKey);
			reportLogger
					.fatal("Block is already deleted for transaction id : " + acknowledgeRequest.getTransactionId());
			customResponse = new CustomSuccessResponse<>();
			customResponse.setMessage(BLOCK_ALREADY_DELETED);
			return customResponse;
		}
		final Block block = blockService.getBlock(combinedKey);

		final String combineData = transactionFactory.getObject().createSignatureCombineString(
				acknowledgeRequest.getTransactionId(), acknowledgeRequest.getTag(),
				acknowledgeRequest.getWalletAddress(), acknowledgeRequest.getWalletPublicKey());

		final boolean isSignatureValid = blockService.isSignatureValid(acknowledgeRequest.getWalletPublicKey(),
				acknowledgeRequest.getSignature(), combineData);

		if (!isSignatureValid) {
			logger.warn("Signature sent does not match with calculated signature for combined key: " + combinedKey);
			reportLogger.fatal("Signature validation failed for id : " + acknowledgeRequest.getTransactionId());
			customResponse = new CustomErrorResponse<>();
			customResponse.setMessage(SIGN_NOT_VALID);
			return customResponse;
		}

		final boolean isCrcSignature = transactionFactory.getObject().verifyCrcAndDelete(block, acknowledgeRequest);
		if (!isCrcSignature) {
			logger.warn(
					"CRC sent does not match with calculated crc it's not gonna process it further for combined key: "
							+ combinedKey);
			reportLogger.fatal("CRC validation failed for transaction id : " + acknowledgeRequest.getTransactionId());
			customResponse = new CustomErrorResponse<>();
			customResponse.setMessage(MSG_NOT_VALID);
			return customResponse;
		}
		reportLogger.fatal("For acknowledge reqest CRC validated successfully for transaction id : "
				+ acknowledgeRequest.getTransactionId());
		logger.info("For acknowledge reqest CRC validated successfully for combined key: " + combinedKey);
		final boolean flag = blockService.checkAndNotify(acknowledgeRequest.getCrc(), acknowledgeRequest.getTag(),
				acknowledgeRequest.getTransactionId());
		if (!flag) {
			reportLogger.fatal(DELETED_ALRDY_RECEIVED + " for transaction id " + acknowledgeRequest.getTransactionId());
			logger.info(DELETED_ALRDY_RECEIVED + " for combine key " + combinedKey);
			customResponse = new CustomErrorResponse<>();
			customResponse.setMessage(DELETED_ALRDY_RECEIVED);
			return customResponse;
		}
		customResponse = new CustomSuccessResponse<>();
		logger.info(BLOCK_DELETED_SUCCESSFULLY + " for combine key " + combinedKey);
		customResponse.setMessage(BLOCK_DELETED_SUCCESSFULLY);
		return customResponse;
	}

	@ResponseBody
	@ApiOperation(value = "Api used to download the file to correspond fileId.")
	@RequestMapping(value = GET_FILE, method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public FileSystemResource getFilePath(@RequestParam("fileId") final String fileId) {
		logger.info("File download was requested for fileId: " + fileId);
		return blockService.getDownloadFile(fileId);
	}

	@ResponseBody
	@ApiOperation(value = "Api used to get log data.")
	@RequestMapping(value = GET_LOG, method = RequestMethod.GET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Object getLog(@RequestParam("pointerLocation") final String pointerLocation) {
		return blockService.readLog(Long.parseLong(pointerLocation));
	}

	@ResponseBody
	@ApiOperation(value = "Api used to get total statistics of PrivateBlockChain.")
	@RequestMapping(value = GET_STATISTICS, method = RequestMethod.GET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Object getAllBlockDetail(@RequestParam("pageNo") final int pageNo) {
		CustomResponse<StatisticsModel> customResponse = null;
		if (pageNo <= 0) {
			customResponse = new CustomErrorResponse<>();
			customResponse.setMessage("Page No value must be greater than or equal to 1");
			return customResponse;
		}
		customResponse = new CustomSuccessResponse<>();
		customResponse.setMessage("Block status result for page no - " + pageNo);
		final StatisticsModel model = new StatisticsModel().setTotalBlocks(blockService.totalBlocks())
				.setSavedCount(blockService.availableBlocks()).setDeletedCount(blockService.getDeletedBlockCount())
				.setBlocks(blockService.getBlockStatusListByPage(pageNo));

		((CustomSuccessResponse<StatisticsModel>) customResponse).setResultSet(model);
		return customResponse;
	}

	@ResponseBody
	@ApiOperation(value = "Api to retrive crcMap and confirmationMap.")
	@RequestMapping(value = GET_CRC_CONFIRM_MAP, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public CrcConfirmMapModel getMap() {
		return blockService.getBothMap();
	}
}
