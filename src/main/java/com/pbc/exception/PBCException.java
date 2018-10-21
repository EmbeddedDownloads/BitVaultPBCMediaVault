package com.pbc.exception;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.pbc.models.CustomErrorResponse;

@ControllerAdvice
public class PBCException extends ResponseEntityExceptionHandler {

	private final Logger logger = Logger.getLogger(PBCException.class);

	@ExceptionHandler(DataException.class)
	public ResponseEntity<String> handleDataException(final DataException de) {
		logger.error("Global DataException ", de);
		return new ResponseEntity<>(new CustomErrorResponse<String>().setMessage(de.getMessage()).toString(),
				HttpStatus.OK);
	}

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<String> handleServiceException(final ServiceException se) {
		logger.error("Global ServiceException ", se);
		return new ResponseEntity<>(new CustomErrorResponse<String>().setMessage(se.getMessage()).toString(),
				HttpStatus.OK);
	}

	@ExceptionHandler(BlockProcessingException.class)
	public ResponseEntity<String> handleBlockNotFoundException(final BlockProcessingException bpe) {
		logger.error("Global BlockProcessingException ", bpe);
		return new ResponseEntity<>(new CustomErrorResponse<String>().setMessage(bpe.getMessage()).toString(),
				HttpStatus.OK);
	}

	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<String> handleFileNotFoundException(final FileNotFoundException fne) {
		logger.error("Global FileNotFoundException ", fne);
		return new ResponseEntity<>(new CustomErrorResponse<String>().setMessage(fne.getMessage()).toString(),
				HttpStatus.OK);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGlobalException(final Exception e) {
		logger.error("Global Exception ", e);
		return new ResponseEntity<>(new CustomErrorResponse<String>().setMessage(e.getMessage()).toString(),
				HttpStatus.OK);
	}
}