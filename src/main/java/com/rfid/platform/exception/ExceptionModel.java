package com.rfid.platform.exception;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExceptionModel implements Serializable {

	private Integer status;
	private String code;
	private String message;
	private String url;

}
