package com.pvai.qa.testdata;

public interface HttpStatus {
	
	// --- 2xx Success ---
	
	public static final int RESP_OK = 200;
	
	// --- 4xx Client Error ---
	
	public static final int RESP_BAD_REQUEST = 400;
	
	public static final int RESP_UNAUTHORIZED = 401;
	
	public static final int RESP_NOT_FOUND = 404;
	
	// --- 5xx Server Error ---
	
	public static final int RESP_INTERNAL_SERVER_ERROR = 500;
	
	public static final int RESP_BAD_GATEWAY = 502;
	
	public static final int RESP_GATEWAY_TIMEOUT = 504;

}
