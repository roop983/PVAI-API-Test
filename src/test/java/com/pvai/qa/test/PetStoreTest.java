package com.pvai.qa.test;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pvai.qa.testdata.Constants;
import com.pvai.qa.testdata.HttpStatus;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class PetStoreTest {
	 @Test
	    void getFindByStatus(){
	        Response response = get("https://petstore.swagger.io/v2/pet/findByStatus?status=available");
	        System.out.println(response.asString());
	        int statusCode = response.getStatusCode();
	        Assert.assertEquals(statusCode, 200,"Response code doesn't match");
	    }

	    @Test
	    void findPetByIdPositiveTest(){
	        given()
	        .baseUri(Constants.BASE_URI)
	        .accept(ContentType.JSON)
			// When
			.when()
				.get("/v2/pet/"+Constants.ID_SOLD)
			// Then
	            .then()
	            	.assertThat()
	                .statusCode(HttpStatus.RESP_OK)
	                .body("id", equalTo(Constants.ID_SOLD))
	                .body("category.id", equalTo(Constants.ID_CATEGORY))
	                .body("category.name", equalTo(Constants.CATEGORY_NAME_SOLD))
	                .body("name", equalTo(Constants.NAME_SOLD))
	                .body("status", equalTo(Constants.STATUS_SOLD))
	                .header("content-type", equalTo("application/json"));
	                
	    }
	    
	    
	    @Test
	    void findPetByIdNegativeTest(){
	        given()
	        .baseUri("https://petstore.swagger.io")
	        .accept(ContentType.JSON)
			// When
			.when()
				.get("/v2/pet/9223372036854023000")
			// Then
	            .then()
	            	.assertThat()
	                .statusCode(404)
	                .body("code", equalTo(1))
	                .body("type", equalTo("error"))
	                .body("message", equalTo("Pet not found"))
	                .header("content-type", equalTo("application/json"));
	                
	    }
	    
	    @Test
	    void addNewPetToStore(){
	    	
	    	// Creating a File instance
	        File jsonData = new File("src/test/resources/Payloads/jsondemo.json");
	        
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
//	                .body("id", equalTo(7574746))
//	                .body("category.id", equalTo(1))
//	                //.body("category.name", equalTo("Cat"))
//	                .body("category.name", equalTo("Cat"))
//	                .body("name", equalTo("Hardy"))
//	                .body("status", equalTo("sold"))
	                .header("content-type", equalTo("application/json"))
	                .log().all();
	               
	                
	    }
	    
	    
	    @Test
	    void test1(){
	    	
	    	// Creating a File instance
	        //File jsonData = new File("src/test/resources/Payloads/jsondemo.json");
	        
	        //String jsonString = "{\"username\" : \"admin\",\"password\" : \"password123\"}";
			
	        given()
	        .baseUri("https://petstore.swagger.io/v2/pet")
	        .accept(ContentType.JSON)
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
	                .statusCode(HttpStatus.RESP_OK)
//	               
	                //.header("content-type", equalTo("application/json"))
	                .log().all();
	                
	    }
}
