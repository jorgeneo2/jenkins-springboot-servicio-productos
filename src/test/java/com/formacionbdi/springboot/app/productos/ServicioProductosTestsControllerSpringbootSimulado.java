package com.formacionbdi.springboot.app.productos;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formacionbdi.springboot.app.commons.models.entity.Producto;
import com.formacionbdi.springboot.app.productos.models.controllers.ProductoController;
import com.formacionbdi.springboot.app.productos.models.service.IProductoService;

@Tag("tests_simulado")
@WebMvcTest(ProductoController.class)
class ServicioProductosTestsControllerSpringbootSimulado {

	@Autowired
    private MockMvc mvc;
	
	@MockBean
	IProductoService service; //PUEDE SER LLAMADO A LA INTERFAZ O A LA IMPLEMENTACION
	
	ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }
	
	@Test
	void contextLoadsFindbyIdSpringBootControllerSimulado() throws Exception {
		
		// Given
		Optional<Producto> producto = Optional.ofNullable(new Producto()); 
		producto.orElseThrow().setId(1L);		
		producto.orElseThrow().setNombre("nevera");
		producto.orElseThrow().setPrecio(1000D);

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		header.add("request-algun-head", "valor con HttpHeaders");
		header.add("request-name-unHeaderMas", "otro valor unHeaderMas"); //este es obligaotrio porque en el controller tiene @RequestHeader(name="request-name-unHeaderMas") 
	    
		
		// When
		when(service.findById(anyLong())).thenReturn(producto.orElseThrow());
		
		mvc.perform(get("/ver/1").headers(header).contentType(MediaType.APPLICATION_JSON))
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("nevera"))
                .andExpect(jsonPath("$.precio").value("1000.0"))
                .andReturn().getResponse().containsHeader("response-algun-head");
                	    
						
		
	}
	
	@Test
	void contextLoadsListarSpringBootControllerSimulado() throws Exception {
		
		// Given
		Optional<Producto> producto = Optional.ofNullable(new Producto()); 
		producto.orElseThrow().setId(1L);		
		producto.orElseThrow().setNombre("nevera");
		producto.orElseThrow().setPrecio(1000D);
		
		List<Producto> listaProductos = new ArrayList<Producto> ();
		
		listaProductos.add(producto.get());
		
		// When
		when(service.findAll()).thenReturn(listaProductos);
		
		mvc.perform(get("/listar").contentType(MediaType.APPLICATION_JSON))
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre", is("nevera")))
                .andExpect(jsonPath("$[0].precio").value("1000.0"))
                .andExpect(jsonPath("$.length()", is(1)))
                ;
						
		System.out.println("fin");
	}
	
	@Test
	void contextLoadsSaveSpringBootControllerSimulado() throws Exception {
		
		// Given
		Optional<Producto> producto = Optional.ofNullable(new Producto()); 
		producto.orElseThrow().setId(1L);
		producto.orElseThrow().setNombre("nevera");
		producto.orElseThrow().setPrecio(1000D);
		producto.orElseThrow().setCreateAt(Date.valueOf(LocalDate.now()));
		
		
		Map<String, Object> response = new HashMap<>();
        response.put("id", 1);
        response.put("nombre", "nevera");
        response.put("precio", 1000.0);
        response.put("createAt", LocalDate.now().toString());
        response.put("port", null);
		
		// When
		
		//Simulando y pasando la respuesta a el service
		//when(service.save(any())).thenReturn(producto.orElseThrow());
		
		//Simulando y construyendo la respuesta en el service		
		when(service.save(any())).then(invocation ->{
			Producto pro = invocation.getArgument(0);
			pro.setId(1L);
			pro.setNombre("nevera");
			pro.setPrecio(1000D);
			pro.setCreateAt(Date.valueOf(LocalDate.now()));
            return pro;
        });
		
		mvc.perform(post("/crear").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(producto)))
        // Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("nevera"))
                .andExpect(jsonPath("$.precio").value(1000))
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                ;
						
		verify(service).save(any());
		
	}

}
