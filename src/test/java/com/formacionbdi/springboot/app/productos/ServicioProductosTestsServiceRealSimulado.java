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
import org.springframework.boot.test.context.SpringBootTest;

import com.formacionbdi.springboot.app.commons.models.entity.Producto;
import com.formacionbdi.springboot.app.productos.models.dao.IProductoDao;
import com.formacionbdi.springboot.app.productos.models.service.ProductoServiceImpl;

@Tag("tests_real_simulado")
@SpringBootTest //ACTIVA CONEXIONES A BASES DE DATOS originales, es decir que no tienen el scope test, toma el scope runtime
//@DataJpaTest	// ACTIVA CONEXIONES A BASES DE DATOS tiene que tener relacion con conexion a base de datos con el scope test en el pom.xml

	/*
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>test</scope>
		</dependency>
	 */
public class ServicioProductosTestsServiceRealSimulado {

	
	//FORMA PARA HACER LLAMADO REALES A METODOS QUE NO SE SIMULEN
	//O EN CASO DE ADICIONARLE UN WHEN SE REALIZA LA SIMULACION DONDE PUEDE QUEDAR HIBIRIDO
	@Spy
	IProductoDao productoDao; //PUEDE SER LLAMADO A LA IMPLEMENTACION Y NO A LA INTERFAZ: 
								// si es llamado a la INTERFAZ se debe realizar la simulacion, de lo contrario hay error
								// si es llamado a la IMPLEMENTACION se puede hacer simulacion o real
	
	@InjectMocks
	ProductoServiceImpl serviceImpl; //TIENE QUE SER LLAMADO A LA IMPLEMENTACION Y NO A LA INTERFAZ

			
	@Test
	void contextLoadsSpyServiceRealSimulado() {

		Optional<Producto> producto = Optional.ofNullable(new Producto()); 
		producto.orElseThrow().setId(1L);		
		producto.orElseThrow().setNombre("nevera");
		producto.orElseThrow().setPrecio(1000D);
		
		when(productoDao.findById(anyLong())).thenReturn(producto); //En caso de no tener una referencia de simulacion como ejemplo WHEN, se toma como un llamdo real y no simulado mock
		
		Producto productoDB = serviceImpl.findById(1);
		
		assertEquals("nevera", productoDB.getNombre());
		
		verify(productoDao).findById(anyLong());			
		
	}
	
}
