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
				.get("/v2/pet/7574746")
			// Then
	            .then()
	            	.assertThat()
	                .statusCode(HttpStatus.RESP_OK)
	                .body("id", equalTo(7574746))
	                .body("category.id", equalTo(1))
	                //.body("category.name", equalTo("Cat"))
	                .body("category.name", equalTo("Cat"))
	                .body("name", equalTo("Hardy"))
	                .body("status", equalTo("sold"))
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
	        
	        String jsonPayload = "{\\r\\n  \\\"id\\\": 9223372036854604000,\\r\\n  \\\"category\\\": {\\r\\n    \\\"id\\\": 0,\\r\\n    \\\"name\\\": \\\"string\\\"\\r\\n  },\\r\\n  \\\"name\\\": \\\"doggie\\\",\\r\\n  \\\"photoUrls\\\": [\\r\\n    \\\"string\\\"\\r\\n  ],\\r\\n  \\\"tags\\\": [\\r\\n    {\\r\\n      \\\"id\\\": 0,\\r\\n      \\\"name\\\": \\\"string\\\"\\r\\n    }\\r\\n  ],\\r\\n  \\\"status\\\": \\\"sold\\\"\\r\\n}";
	 
	        given()
	        .baseUri("https://petstore.swagger.io")
	        .accept(ContentType.JSON)
	        .body(jsonPayload)
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
}
