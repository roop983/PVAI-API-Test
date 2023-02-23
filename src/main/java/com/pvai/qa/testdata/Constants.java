package com.pvai.qa.testdata;

public class Constants {
	
	//Common
	 public static final String BASE_URI="https://petstore.swagger.io";
	 public static final String UNKNOWN_TYPE="unknown";
	 public static final String ERROR_MSG_NOT_FOUND="not found";
	 public static final String ERRMSG_BAD_HAPPENED="something bad happened";
	 public static final int SUCCESS_CODE=200;
	 public static final int BAD_REQUEST_CODE=400;
	 public static final int NOT_FOUND_CODE=404;
	 public static final int UNSUPPORTED_FORMAT=415;
	 public static final int INTERNAL_SERVER_ERROR=500;
	 
	 //Content Types
	 public static final String APPLICATION_JSON="application/json";
	 public static final String APPLICATION_FORM_URL_ENCODED="application/x-www-form-urlencoded";
	 public static final String MULTIPART_FORM_DATA="multipart/form-data";
	 public static final String HTML_TEXT_CHARSET_ISO_8859="text/html; charset=ISO-8859-1";
	 
	 
	 //Exception Messages
	 public static final String MIME_PARSING_EXCEPTION="org.jvnet.mimepull.MIMEParsingException: Missing start boundary";
	 public static final String NUMBER_FORMAT_EXCEPTION="java.lang.NumberFormatException";
	 
	 //pet By Status
	 public static final String STATUS_SOLD="sold";
	 public static final String STATUS_AVAILABLE="available";
	 public static final String STATUS_PENDING="pending";
	 
	 //pet By ID - Valid ID
	 public static final int ID_SOLD=7574746;
	 public static final int ID_CATEGORY=1;
	 public static final String CATEGORY_NAME_SOLD="Cat";
	 public static final String NAME_SOLD="Hardy";
	 public static final long INVALID_ID=9223372036854023000L;
	 public static final String PET_NOT_FOUND_ERRORMSG="Pet not found";
	 public static final int PET_NOT_FOUND_ERROR_CODE=1;
	 public static final String PET_NOT_FOUND_ERROR_TYPE="error";
	 
	 //addNewPet
	 public static final long ID_NEW=9223372036854604000L;
	 public static final int ID_CATEGORY_NEW=0;
	 public static final String NAME_NEW="doggie";
	
	 
	 //fileUpload
	 public static final int ID_UPLOAD=7574747;
	 public static final String METADATA_FILE_UPLOAD="File Upload";
	 
	 
	 //updateExistingPet
	 public static final int ID_EXISTING=922337;
	 public static final String NAME_EXISTING="bunny";
	 
	 //FormData Update
	 public static final int ID_FOR_UPDATE=112;
	
	 



}
