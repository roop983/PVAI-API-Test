package com.pvai.qa.test;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.pvai.qa.testdata.Constants;
import com.pvai.qa.testdata.HttpStatus;

import io.restassured.http.ContentType;
import io.restassured.response.Response;


public class PetStoreTest {
	
	Number petIdToBeDeleted = 0;
	Number findPetId = 0;
	String findPetName = "";
	String petStatus = "";
	 
	 @DataProvider(name = "petStatus")
	   public static String[] petStatus() {
	      return new String[] {Constants.STATUS_AVAILABLE, Constants.STATUS_PENDING, Constants.STATUS_SOLD};
	   } 
	
	//This test will find Pets with status : available, pending and sold. Uses Data Provider to pass the parameters
	 @Test(priority=1, dataProvider = "petStatus")
	    void findPetByStatus(String statusName){
		 
		 Response response = 
	        given()
	        .baseUri(Constants.BASE_URI)
	        .accept(Constants.APPLICATION_JSON)
			// When
			.when()
				.get("/v2/pet/findByStatus?status="+statusName)
			// Then
	            .then()
	            	.assertThat()
	                .statusCode(HttpStatus.RESP_OK)
	                .header("content-type", equalTo(Constants.APPLICATION_JSON))
	                .extract().response();
		 
		// put all of the status into a list
		 List<String> petStatusList = response.path("status");
		 // check that they are all 'available'
	    for (String state : petStatusList)
	    {
	    	Assert.assertEquals(state, statusName);
	    }
	    
	 }
	 
	

	//This test will add a new Pet. The payload body is provided using File.
    @Test(priority=2)
    void addNewPetToStorePositiveTest(){
    	
    	// Creating a File instance
        File jsonData = new File("src/test/resources/Payloads/addNewPet.json");
        
        Response response = 
        given()
        .baseUri("https://petstore.swagger.io")
        .contentType(Constants.APPLICATION_JSON)
        .body(jsonData)
		// When
		.when()
			.post("/v2/pet")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_OK)
                .body("id", equalTo(Constants.ID_NEW))
                .body("category.id", equalTo(Constants.ID_CATEGORY_NEW))
                .body("name", equalTo(Constants.NAME_NEW))
                .body("status", equalTo(Constants.STATUS_AVAILABLE))
                .header("content-type", equalTo(Constants.APPLICATION_JSON))
                .extract().response();
        
        //Extract response as String
		String jsonString = response.getBody().asString();
		 
		//Extract the Pet Name, Status and Id to be used later by the method: findPetByIdPositiveTest
		findPetName = JsonPath.read(jsonString, "$.name").toString();
		petStatus = JsonPath.read(jsonString, "$.status").toString();
		findPetId = JsonPath.read(jsonString, "$.id");
                
    }
	    
  //This test will add a new Pet using an Invalid Id, Its a negative test scenario and throws 500 server error.
    @Test(priority=3)    
    void addNewPetToStoreWithStringAsIdNegativeTest(){
    	
    	// Creating a File instance
        File jsonData = new File("src/test/resources/Payloads/addNewPetNegativeTest.json");
        
        given()
        .baseUri("https://petstore.swagger.io")
        .contentType(Constants.APPLICATION_JSON)
        .body(jsonData)
		// When
		.when()
			.post("/v2/pet")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_INTERNAL_SERVER_ERROR)
                .body("code", equalTo(Constants.INTERNAL_SERVER_ERROR))
                .body("type", equalTo(Constants.UNKNOWN_TYPE))
                .body("message", equalTo(Constants.ERRMSG_BAD_HAPPENED))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));

                
    }
    
  //This test will add a new Pet using a Long Name, Long Status and a valid Photo Url. Its a positive test scenario
    @Test(priority=4)    
    void addNewPetToStoreWithLongNameAndLongStatusPositiveTest(){
    	
    	// Creating a File instance
        File jsonData = new File("src/test/resources/Payloads/addNewPetLongPhotoUrlLongStatus.json");
        
        given()
        .baseUri("https://petstore.swagger.io")
        .contentType(Constants.APPLICATION_JSON)
        .body(jsonData)
		// When
		.when()
			.post("/v2/pet")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_OK)
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
                
    }
    
  //This test will add a new Pet with a long Category Name. It throws 500 Server error.
    @Test(priority=5)    
    void addNewPetToStoreWithLongCategoryIdServerError(){
    	
    	// Creating a File instance
        File jsonData = new File("src/test/resources/Payloads/addNewPetLongCategoryId.json");
        
        given()
        .baseUri("https://petstore.swagger.io")
        .contentType(Constants.APPLICATION_JSON)
        .body(jsonData)
		// When
		.when()
			.post("/v2/pet")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_INTERNAL_SERVER_ERROR)
                .body("code", equalTo(Constants.INTERNAL_SERVER_ERROR))
                .body("type", equalTo(Constants.UNKNOWN_TYPE))
                .body("message", equalTo(Constants.ERRMSG_BAD_HAPPENED))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));

                
    }
    
  //This test will find an existing Pet using an ID fetched from the method : addNewPetToStorePositiveTest
    @Test(priority=6, dependsOnMethods = { "addNewPetToStorePositiveTest" })
    void findPetByIdPositiveTest(){
    	
        given()
        .baseUri(Constants.BASE_URI)
        .accept(Constants.APPLICATION_JSON)
		// When
		.when()
			.get("/v2/pet/"+findPetId)
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_OK)
                .body("id", equalTo(findPetId))
                .body("name", equalTo(findPetName))
                .body("status", equalTo(petStatus))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
   
    }
    
  //This test will find a Pet using an alphanumeric ID. This is a negative scenario
    @Test(priority=7)
    void findPetByIdWithAlphaNumericIdNegativeTest(){
    	
    	String id = "abc12334";
        given()
        .baseUri(Constants.BASE_URI)
        .accept(Constants.APPLICATION_JSON)
		// When
		.when()
			.get("/v2/pet/"+id)
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_NOT_FOUND)
                .body("code", equalTo(Constants.NOT_FOUND_CODE))
                .body("type", equalTo(Constants.UNKNOWN_TYPE))
                .body("message", containsString(Constants.NUMBER_FORMAT_EXCEPTION))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
   
    }
    
  //This test will find a Pet using an Invalid ID. This is a negative scenario
    @Test(priority=8)
    void findPetByIdWIthInvalidIdNegativeTest(){
        given()
        .baseUri("https://petstore.swagger.io")
        .accept(Constants.APPLICATION_JSON)
		// When
		.when()
			.get("/v2/pet/"+Constants.INVALID_ID)
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_NOT_FOUND)
                .body("code", equalTo(Constants.PET_NOT_FOUND_ERROR_CODE))
                .body("type", equalTo(Constants.PET_NOT_FOUND_ERROR_TYPE))
                .body("message", equalTo(Constants.PET_NOT_FOUND_ERRORMSG))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
                
    }
    
  //This test will update a Pet with Form data using an ID fetched from the method : addNewPetToStorePositiveTest
    @Test(priority=9, dependsOnMethods = { "addNewPetToStorePositiveTest" })
    void updatePetWithFormData(){
    	
    	given()
        .baseUri("https://petstore.swagger.io")
        .accept(Constants.APPLICATION_JSON)
        .contentType(Constants.APPLICATION_FORM_URL_ENCODED)
        .queryParam("name", Constants.NAME_NEW)
        .queryParam("status", Constants.STATUS_SOLD)
		// When
		.when()
			.post("/v2/pet/"+findPetId)
		// Then
            .then()
            .assertThat()
            .statusCode(HttpStatus.RESP_OK)
            .body("code", equalTo(Constants.SUCCESS_CODE))
            .body("type", equalTo(Constants.UNKNOWN_TYPE))
            .body("message", equalTo(String.valueOf(findPetId)))
            .header("content-type", equalTo(Constants.APPLICATION_JSON));
      
    }
    
  //This test will update a Pet with Form data using an Unknown ID. Its a negative scenario
    @Test(priority=10)
    void updatePetWithFormDataWithUnknownId(){
    	
    	  given()
	        .baseUri("https://petstore.swagger.io")
	        .accept(Constants.APPLICATION_JSON)
	        .contentType(Constants.APPLICATION_FORM_URL_ENCODED)
	        .queryParam("name", Constants.NAME_NEW)
	        .queryParam("status", Constants.STATUS_SOLD)
			// When
			.when()
				.post("/v2/pet/"+Constants.ID_SOLD)
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_NOT_FOUND)
                .body("code", equalTo(Constants.NOT_FOUND_CODE))
                .body("type", equalTo(Constants.UNKNOWN_TYPE))
                .body("message", equalTo(Constants.ERROR_MSG_NOT_FOUND))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
                   
    }
    
  //This test will update a Pet with Form data using a Long value for ID. Its a negative scenario.
    @Test(priority=11)
    void updatePetWithFormDataWithLongId(){
    	
    	  given()
	        .baseUri("https://petstore.swagger.io")
	        .accept(Constants.APPLICATION_JSON)
	        .contentType(Constants.APPLICATION_FORM_URL_ENCODED)
	        .queryParam("name", Constants.NAME_NEW)
	        .queryParam("status", Constants.STATUS_SOLD)
			// When
			.when()
				.post("/v2/pet/94605284000000000000000")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_NOT_FOUND)
                .body("code", equalTo(Constants.NOT_FOUND_CODE))
                .body("type", equalTo(Constants.UNKNOWN_TYPE))
                .body("message", containsString(Constants.NUMBER_FORMAT_EXCEPTION))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
                
                
    }
    
  //This test will delete a Pet that is created with the method: addNewPetToStorePositiveTest
  	 @Test(priority=12, dependsOnMethods = { "addNewPetToStorePositiveTest" })
  	    void deletePetById(){
  	    	  given()
  		        .baseUri("https://petstore.swagger.io")
  		        .accept(Constants.APPLICATION_JSON)
  				// When
  				.when()
  					.delete("/v2/pet/"+findPetId)
  				// Then
  		            .then()
  		            	.assertThat()
  		                .statusCode(HttpStatus.RESP_OK)
  		                .body("code", equalTo(Constants.SUCCESS_CODE))
  		                .body("type", equalTo(Constants.UNKNOWN_TYPE))
  		                .body("message", equalTo(String.valueOf(findPetId)))
  		                .header("content-type", equalTo(Constants.APPLICATION_JSON));

  		      
  		    }
  	 
  	//This test will delete a Pet using an Invalid Id, Its a negative test scenario
  	 @Test(priority=13)
  	    void deletePetByIdNegativeTest(){
  	    	  given()
  		        .baseUri("https://petstore.swagger.io")
  		        .accept(Constants.APPLICATION_JSON)
  				// When
  				.when()
  					.delete("/v2/pet/"+Constants.INVALID_ID)
  				// Then
  		            .then()
  		            	.assertThat()
  		                .statusCode(HttpStatus.RESP_NOT_FOUND);
  		                
  		    }
    
  //This test will upload an Image using an Id, metadata and File upload.
    @Test(priority=14)
    void uploadImagePositiveTest(){
    	
    	// Creating a File instance
        File uploadFile = new File("src/test/resources/Payloads/TestFileUpload.png");
        String fileName = uploadFile.getName();
        
        given()
        .baseUri("https://petstore.swagger.io")
        .accept(Constants.APPLICATION_JSON)
        .contentType(Constants.MULTIPART_FORM_DATA)
        .multiPart(uploadFile)
        .multiPart("additionalMetadata", Constants.METADATA_FILE_UPLOAD)
		// When
		.when()
			.post("/v2/pet/"+Constants.ID_UPLOAD+"/uploadImage")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_OK)
                .body("code", equalTo(Constants.SUCCESS_CODE))
                .body("type", equalTo(Constants.UNKNOWN_TYPE))
                .body("message", containsString(fileName))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
                     
                
    }
    
  //This test will upload an Image using an alphanumeric Id, and valid metadata and File upload. This is a negative scenario.
    @Test(priority=15)
    void uploadImageNegativeTestInvalidId(){
    	
    	// Creating a File instance
        File uploadFile = new File("src/test/resources/Payloads/TestFileUpload.png");
        String fileName = uploadFile.getName();
        
        given()
        .baseUri("https://petstore.swagger.io")
        .accept(Constants.APPLICATION_JSON)
        .contentType(Constants.MULTIPART_FORM_DATA)
        .multiPart(uploadFile)
        .multiPart("additionalMetadata", Constants.METADATA_FILE_UPLOAD)
		// When
		.when()
			.post("/v2/pet/abc/uploadImage")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_NOT_FOUND)
                .body("code", equalTo(Constants.NOT_FOUND_CODE))
                .body("type", equalTo(Constants.UNKNOWN_TYPE))
                .body("message", containsString(Constants.NUMBER_FORMAT_EXCEPTION))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
                  
                
    }
    
  //This test will upload an Image with no Id, but with valid metadata and File upload. This is a negative scenario.
    @Test(priority=16)
    void uploadImageNegativeTestNoId(){
    	
    	// Creating a File instance
        File uploadFile = new File("src/test/resources/Payloads/TestFileUpload.png");
        String fileName = uploadFile.getName();
        
        given()
        .baseUri("https://petstore.swagger.io")
        .accept(Constants.APPLICATION_JSON)
        .contentType(Constants.MULTIPART_FORM_DATA)
        .multiPart(uploadFile)
        .multiPart("additionalMetadata", Constants.METADATA_FILE_UPLOAD)
		// When
		.when()
			.post("/v2/pet/uploadImage")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_UNSUPPORTED_FORMAT)
                .body("code", equalTo(Constants.UNSUPPORTED_FORMAT))
                .body("type", equalTo(Constants.UNKNOWN_TYPE))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
                     
                
    }
    
  //This test will upload an Image using a valid Id, but No metadata and no File. This is a negative scenario.
    @Test(priority=17)
    void uploadImageNegativeTestNoFileNoMetadata(){
        
        given()
        .baseUri("https://petstore.swagger.io")
        .accept(Constants.APPLICATION_JSON)
        .contentType("multipart/form-data")
		// When
		.when()
		.post("/v2/pet/"+Constants.ID_UPLOAD+"/uploadImage")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_BAD_REQUEST)
                .body("code", equalTo(Constants.BAD_REQUEST_CODE))
                .body("type", equalTo(Constants.UNKNOWN_TYPE))
                .body("message", equalTo(Constants.MIME_PARSING_EXCEPTION))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
                
    }
    
  //This test will upload an Image using a valid Id and metadata but no File. This is a negative scenario.
    @Test(priority=18)
    void uploadImageNegativeTestNoFile(){

        given()
        .baseUri("https://petstore.swagger.io")
        .accept(Constants.APPLICATION_JSON)
        .contentType("multipart/form-data")
        .multiPart("additionalMetadata", Constants.METADATA_FILE_UPLOAD)
		// When
		.when()
		.post("/v2/pet/"+Constants.ID_UPLOAD+"/uploadImage")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_INTERNAL_SERVER_ERROR)
                .header("content-type", equalTo(Constants.HTML_TEXT_CHARSET_ISO_8859));
                     
                
    }
    
    //This test will update an existing Pet. The body payload is provided through file.
    @Test(priority=19)
    void updateExistingPet(){
    	
    	// Creating a File instance
        File jsonData = new File("src/test/resources/Payloads/updateExistingPet.json");
        
        given()
        .baseUri("https://petstore.swagger.io")
        .contentType(Constants.APPLICATION_JSON)
        .body(jsonData)
		// When
		.when()
			.put("/v2/pet")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_OK)
                .body("id", equalTo(Constants.ID_EXISTING))
                .body("name", equalTo(Constants.NAME_EXISTING))
                .body("status", equalTo(Constants.STATUS_AVAILABLE))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
                
                
    }
    
  //This test will update an existing Pet with an unknown ID.  This is a negative scenario.
    @Test(priority=20)
    void updateExistingPetUnknownIdNegativeTest(){
    	
    	// Creating a File instance
        File jsonData = new File("src/test/resources/Payloads/updateExistingPetUnknownId.json");
        
        given()
        .baseUri("https://petstore.swagger.io")
        .contentType(Constants.APPLICATION_JSON)
        .body(jsonData)
		// When
		.when()
			.put("/v2/pet")
		// Then
            .then()
            	.assertThat()
                .statusCode(HttpStatus.RESP_INTERNAL_SERVER_ERROR)
                .body("code", equalTo(Constants.INTERNAL_SERVER_ERROR))
                .body("type", equalTo(Constants.UNKNOWN_TYPE))
                .body("message", equalTo(Constants.ERRMSG_BAD_HAPPENED))
                .header("content-type", equalTo(Constants.APPLICATION_JSON));
                 
    }
    
	  
}
