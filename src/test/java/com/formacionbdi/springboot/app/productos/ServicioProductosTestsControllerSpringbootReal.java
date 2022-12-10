package com.formacionbdi.springboot.app.productos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formacionbdi.springboot.app.commons.models.entity.Producto;

@Tag("tests_real_integracion")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT) //SE CREA UN AMBIENTE WEB PARA REALIZAR PETICIONES
class ServicioProductosTestsControllerSpringbootReal {
	
	ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }
    
    @Autowired
    private WebTestClient client; //SE TIENE QUE INYECTAR EN EL POM.XML LA DEPENDENCIA con el scope test
    
	/*
		<dependency> 
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-webflux</artifactId> 
			<scope>test</scope>
		</dependency>
	 */
	
	@Test
	@Order(1)
	void contextLoadsFindbyIdSpringBootControllerReal() throws Exception {
		
		// Given
		Producto producto = new Producto(); 
		producto.setId(1L);		
		producto.setNombre("panasonic");
		producto.setPrecio(100D);
		producto.setCreateAt(Date.valueOf(LocalDate.now()));
		producto.setPort(8001);
		
		// When
				
		//client.get().uri("http://localhost:8001/ver/1") 	//SUBIMOS EL PROYECTO Y LE INDICAMOS LA URL DONDE SE ENCUENTRA
		client.get().uri("/ver/1") 							//SUBIMOS LO NECESARIO QUE DEBE USAR EL PROYECTO (COMO EL CONSUL), YA QUE SE AUTO SUBE 
															//Y NO LE INDICAMOS LA URL DONDE SE ENCUENTRA SI SE ENCUENTRA EN EL MISMO PROYECTO
				.exchange()
        // Then
				.expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                
                //TEST POR STRING RESPONSE
//                .expectBody()
//                .jsonPath("$.nombre").isEqualTo("panasonic")
//                .jsonPath("$.precio").isEqualTo("100.0")
//                .json(objectMapper.writeValueAsString(producto))
                
              //TEST POR CLASS RESPONSE
                .expectBody(Producto.class)
                .consumeWith(response -> {
                	Producto pro = response.getResponseBody();
                    assertNotNull(pro);
                    assertEquals("panasonic", pro.getNombre());
                    assertEquals(100.0, pro.getPrecio());
                });					
		
	}
	
	@Test
	@Order(2)
	void contextLoadsSaveSpringBootControllerReal() throws Exception {
		
		// Given
		Producto producto = new Producto(); 
		producto.setNombre("clonik");
		producto.setPrecio(200D);
		producto.setCreateAt(Date.valueOf(LocalDate.now()));		
		
		// When
				
		client.post().uri("/crear") 
				.contentType(MediaType.APPLICATION_JSON)		
				.bodyValue(producto)
				.exchange()
        // Then
				.expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Producto.class)                
                .consumeWith(response -> {
                	Producto pro = response.getResponseBody();
                    assertNotNull(pro);
                    assertEquals("clonik", pro.getNombre());
                    assertEquals(200.0, pro.getPrecio());
                });				
		
	}
	

}
