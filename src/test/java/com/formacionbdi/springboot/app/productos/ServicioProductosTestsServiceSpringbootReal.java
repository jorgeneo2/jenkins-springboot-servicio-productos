package com.formacionbdi.springboot.app.productos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.formacionbdi.springboot.app.commons.models.entity.Producto;
import com.formacionbdi.springboot.app.productos.models.dao.IProductoDao;
import com.formacionbdi.springboot.app.productos.models.service.IProductoService;
import com.formacionbdi.springboot.app.productos.models.service.ProductoServiceImpl;

@Tag("tests_real")
@SpringBootTest
public class ServicioProductosTestsServiceSpringbootReal {

	
	@MockBean
	IProductoDao productoDao; 
	
	@Autowired
	IProductoService iProductoService;

			
	@Test
	void contextLoadsSpyServiceRealSimulado() {

		Optional<Producto> producto = Optional.ofNullable(new Producto()); 
		producto.orElseThrow().setId(1L);		
		producto.orElseThrow().setNombre("nevera");
		producto.orElseThrow().setPrecio(1000D);
		
		when(productoDao.findById(anyLong())).thenReturn(producto);
		
		Producto productoDB = iProductoService.findById(1);
		
		assertEquals("nevera", productoDB.getNombre());
		
		verify(productoDao).findById(anyLong());			
		
	}
	
}
