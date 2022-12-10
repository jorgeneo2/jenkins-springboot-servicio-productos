package com.formacionbdi.springboot.app.productos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.formacionbdi.springboot.app.commons.models.entity.Producto;
import com.formacionbdi.springboot.app.productos.models.dao.IProductoDao;

@Tag("tests_real")
//@SpringBootTest //ACTIVA CONEXIONES A BASES DE DATOS originales, es decir que no tienen el scope test, toma el scope runtime
@DataJpaTest	// ACTIVA CONEXIONES A BASES DE DATOS tiene que tener relacion con conexion a base de datos con el scope test en el pom.xml

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
class ServicioProductosTestsRepositoryReal {

	
	@Autowired
	IProductoDao iproductoDao; //TIENE QUE SER LLAMADO A LA INTERFAZ Y NO A LA IMPLEMENTACION
	
	
	@Test
	void contextLoadsSpringBootRepositoryReal() {
		Optional<Producto> productoDB = iproductoDao.findById(1L);
		
		assertEquals("panasonic", productoDB.orElseThrow().getNombre());
	}

}
