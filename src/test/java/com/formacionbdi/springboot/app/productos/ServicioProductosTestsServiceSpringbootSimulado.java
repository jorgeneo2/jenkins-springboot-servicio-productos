package com.formacionbdi.springboot.app.productos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.formacionbdi.springboot.app.commons.models.entity.Producto;
import com.formacionbdi.springboot.app.productos.models.dao.IProductoDao;
import com.formacionbdi.springboot.app.productos.models.service.ProductoServiceImpl;

@Tag("tests_simulado")
@SpringBootTest
class ServicioProductosTestsServiceSpringbootSimulado {

	
	@MockBean
	IProductoDao iproductoDao; //TIENE QUE SER LLAMADO A LA INTERFAZ Y NO A LA IMPLEMENTACION
	
	@Autowired
	ProductoServiceImpl service; //SE PUEDE INYECTAR LA INTERFAZ O IMPLEMENTACION
	
	@Test
	void contextLoadsSpringBootServiceSimulado() {

		Optional<Producto> producto = Optional.ofNullable(new Producto()); 
		producto.orElseThrow().setId(1L);		
		producto.orElseThrow().setNombre("nevera");
		producto.orElseThrow().setPrecio(1000D);
		
		when(iproductoDao.findById(anyLong())).thenReturn(producto);
		
		Producto productoDB = service.findById(1);
		
		assertEquals("nevera", productoDB.getNombre());
		
		verify(iproductoDao).findById(anyLong());			
		
	}

}
