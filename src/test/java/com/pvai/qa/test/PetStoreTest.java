package com.pvai.qa.test;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

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
	
	 @Test(dataProvider = "petStatus")
	    void findPetByStatus(String statusName){
		 
		 Response response = 
	        given()
	        .baseUri(Constants.BASE_URI)
	        .accept("application/json")
			// When
			.when()
				.get("/v2/pet/findByStatus?status="+statusName)
			// Then
	            .then()
	            	.assertThat()
	                .statusCode(HttpStatus.RESP_OK)
	                //.body("id", hasSize(164))
	                .header("content-type", equalTo("application/json"))
	                .extract().response();
		 
		 //Extract response as String
		 String jsonString = response.getBody().asString();
		 
		 //Extract the IDs and store in a list
		 List<Long> listId=response.jsonPath().getList("id");
		 
		 //Extract the statuses and store in a string array
		 String[] status=JsonPath.read(jsonString, "$..status").toString().split(",");
		 
		 //Convert the string array to List and then store the unique value in a set
		 Set<String> set = new HashSet(Arrays.asList(status));
		 
		 //If the Set contains unique value i.e Available, compare size of the list IDs with the original response string length
		 //to verify that the JSON response contains Status as Available.
		 if (set.size()==1 && set.contains(statusName)) {
			 Assert.assertTrue(listId.size()==status.length);
		 }
		 
		 if (statusName.equals("available")) {
			  findPetName = JsonPath.read(jsonString, "$[0].name").toString();
			  petStatus = JsonPath.read(jsonString, "$[0].status").toString();
			  findPetId = JsonPath.read(jsonString, "$[0].id");
			 	System.out.println(findPetName);
			 	System.out.println(petStatus);
			 	System.out.println(findPetId);
			 }
		 
		 
		 if (statusName.equals("sold")) {
			petIdToBeDeleted = listId.get(1);
		 	System.out.println(petIdToBeDeleted);
		 }
	    }
	 
	 @Test(dependsOnMethods = { "findPetByStatus" })
	    void deletePetById(){
		 System.out.println("**********");
		 System.out.println(petIdToBeDeleted);
	    	  given()
		        .baseUri("https://petstore.swagger.io")
		        .accept("application/json")
				// When
				.when()
					.delete("/v2/pet/"+petIdToBeDeleted)
				// Then
		            .then()
		            	.assertThat()
		                .statusCode(HttpStatus.RESP_OK)
		                .body("code", equalTo(Constants.SUCCESS_CODE))
		                .body("type", equalTo(Constants.UNKNOWN_TYPE))
		                .body("message", equalTo(String.valueOf(petIdToBeDeleted)))
		                .header("content-type", equalTo("application/json"))
		                .log().all();
		                
		    }
	 
	 @Test
	    void deletePetByIdNegativeTest(){
	    	  given()
		        .baseUri("https://petstore.swagger.io")
		        .accept("application/json")
				// When
				.when()
					.delete("/v2/pet/"+Constants.INVALID_ID)
				// Then
		            .then()
		            	.assertThat()
		                .statusCode(HttpStatus.RESP_NOT_FOUND)
		                .log().all();
		                
		    }
	    

	    @Test(dependsOnMethods = { "findPetByStatus" })
	    void findPetByIdPositiveTest(){
	    	
	    	Number petId= 9223372036854749000L;
	        given()
	        .baseUri(Constants.BASE_URI)
	        .accept("application/json")
			// When
			.when()
				.get("/v2/pet/"+petId)
			// Then
	            .then()
	            	.assertThat()
//	                .statusCode(HttpStatus.RESP_OK)
//	                .body("id", equalTo(Constants.ID_SOLD))
//	                .body("category.id", equalTo(Constants.ID_CATEGORY))
//	                .body("category.name", equalTo(Constants.CATEGORY_NAME_SOLD))
//	                .body("name", equalTo(Constants.NAME_SOLD))
//	                .body("status", equalTo(Constants.STATUS_SOLD))
	                .header("content-type", equalTo("application/json"))
	                .log().all();
	                
	    }
	    
	    
	    @Test
	    void findPetByIdNegativeTest(){
	        given()
	        .baseUri("https://petstore.swagger.io")
	        .accept("application/json")
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
	                .header("content-type", equalTo("application/json"));
	                
	    }
	    
	    @Test
	    void addNewPetToStorePositiveTest(){
	    	
	    	// Creating a File instance
	        File jsonData = new File("src/test/resources/Payloads/addNewPet.json");
	        
	        given()
	        .baseUri("https://petstore.swagger.io")
	        .contentType("application/json")
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
	                .header("content-type", equalTo("application/json"))
	                .log().all();
	                
	                
	    }
	    
	    @Test    
	    void addNewPetToStoreNegativeTest(){
	    	
	    	// Creating a File instance
	        File jsonData = new File("src/test/resources/Payloads/addNewPetNegativeTest.json");
	        
	        given()
	        .baseUri("https://petstore.swagger.io")
	        .contentType("application/json")
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
	                .header("content-type", equalTo("application/json"));
	               
	               
	                
	    }
	    
	    @Test
	    void updatePetWithFormData(){
	    	
	    	given()
	        .baseUri("https://petstore.swagger.io")
	        .accept("application/json")
	        .contentType("application/x-www-form-urlencoded")
	        .queryParam("name", Constants.NAME_NEW)
	        .queryParam("status", Constants.STATUS_SOLD)
			// When
			.when()
				.post("/v2/pet/"+Constants.ID_FOR_UPDATE)
			// Then
	            .then()
	            .assertThat()
//                .statusCode(HttpStatus.RESP_OK)
//                .body("code", equalTo(Constants.SUCCESS_CODE))
//                .body("type", equalTo(Constants.UNKNOWN_TYPE))
//                .body("message", equalTo(String.valueOf(Constants.ID_FOR_UPDATE)))
                .header("content-type", equalTo("application/json"))
                .log().all();
             
	                
	    }
	    
	    
	    @Test
	    void updatePetWithFormDataWithIdNotFound(){
	    	
	    	  given()
		        .baseUri("https://petstore.swagger.io")
		        .accept("application/json")
		        .contentType("application/x-www-form-urlencoded")
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
	                .header("content-type", equalTo("application/json"));
	                   
	    }
	    
	    @Test
	    void updatePetWithFormDataWithLongId(){
	    	
	    	  given()
		        .baseUri("https://petstore.swagger.io")
		        .accept("application/json")
		        .contentType("application/x-www-form-urlencoded")
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
	                .header("content-type", equalTo("application/json"));
	                
	                
	    }
	    
	    
	    @Test
	    void uploadImage(){
	    	
	    	// Creating a File instance
	        File uploadFile = new File("src/test/resources/Payloads/TestFileUpload.png");
	        String fileName = uploadFile.getName();
	        
	        given()
	        .baseUri("https://petstore.swagger.io")
	        .accept("application/json")
	        .contentType("multipart/form-data")
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
	                .header("content-type", equalTo("application/json"));
	                     
	                
	    }
	    
	    
	    @Test
	    void uploadImageNegativeTestInvalidId(){
	    	
	    	// Creating a File instance
	        File uploadFile = new File("src/test/resources/Payloads/TestFileUpload.png");
	        String fileName = uploadFile.getName();
	        
	        given()
	        .baseUri("https://petstore.swagger.io")
	        .accept("application/json")
	        .contentType("multipart/form-data")
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
	                .header("content-type", equalTo("application/json"));
	                  
	                
	    }
	    
	    @Test
	    void uploadImageNegativeTestNoId(){
	    	
	    	// Creating a File instance
	        File uploadFile = new File("src/test/resources/Payloads/TestFileUpload.png");
	        String fileName = uploadFile.getName();
	        
	        given()
	        .baseUri("https://petstore.swagger.io")
	        .accept("application/json")
	        .contentType("multipart/form-data")
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
	                .header("content-type", equalTo("application/json"));
	                     
	                
	    }
	    
	    @Test
	    void uploadImageNegativeTestNoFileNoMetadata(){
	        
	        given()
	        .baseUri("https://petstore.swagger.io")
	        .accept("application/json")
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
	                .header("content-type", equalTo("application/json"));
	                
	    }
	    
	    @Test
	    void uploadImageNegativeTestNoFile(){

	        given()
	        .baseUri("https://petstore.swagger.io")
	        .accept("application/json")
	        .contentType("multipart/form-data")
	        .multiPart("additionalMetadata", Constants.METADATA_FILE_UPLOAD)
			// When
			.when()
			.post("/v2/pet/"+Constants.ID_UPLOAD+"/uploadImage")
			// Then
	            .then()
	            	.assertThat()
	                .statusCode(HttpStatus.RESP_INTERNAL_SERVER_ERROR)
	                .header("content-type", equalTo("text/html; charset=ISO-8859-1"));
	                     
	                
	    }
	    
	    @Test
	    void updateExistingPet(){
	    	
	    	// Creating a File instance
	        File jsonData = new File("src/test/resources/Payloads/updateExistingPet.json");
	        
	        given()
	        .baseUri("https://petstore.swagger.io")
	        .contentType("application/json")
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
	                .header("content-type", equalTo("application/json"));
	                
	                
	    }
	    
	    
	    @Test
	    void updateExistingPetNegativeTest(){
	    	
	    	// Creating a File instance
	        File jsonData = new File("src/test/resources/Payloads/updateExistingPetUnknownId.json");
	        
	        given()
	        .baseUri("https://petstore.swagger.io")
	        .contentType("application/json")
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
	                .header("content-type", equalTo("application/json"));
	                 
	    }
	    
	    
	    
	    @Test
	    void test1(){
	    	
	    	// Creating a File instance
	        //File jsonData = new File("src/test/resources/Payloads/jsondemo.json");
	        
	        //String jsonString = "{\"username\" : \"admin\",\"password\" : \"password123\"}";
			
	        given()
	        .baseUri("https://petstore.swagger.io/v2/pet")
	        .accept("application/json")
		  	.contentType("application/json")
	        .body("{\n"
	        		+ "  \"id\": 9223372036854604000,\n"
	        		+ "  \"category\": {\n"
	        		+ "    \"id\": 0,\n"
	        		+ "    \"name\": \"string\"\n"
	        		+ "  },\n"
	        		+ "  \"name\": \"doggie\",\n"
	        		+ "  \"photoUrls\": [\n"
	        		+ "    \"string\"\n"
	        		+ "  ],\n"
	        		+ "  \"tags\": [\n"
	        		+ "    {\n"
	        		+ "      \"id\": 0,\n"
	        		+ "      \"name\": \"string\"\n"
	        		+ "    }\n"
	        		+ "  ],\n"
	        		+ "  \"status\": \"sold\"\n"
	        		+ "}")
			// When
			.when()
				.post()
			// Then
	            .then()
	            	.assertThat()
	                .statusCode(HttpStatus.RESP_OK);
//	               
	                //.header("content-type", equalTo("application/json"))
	                
	                
	    }
}
