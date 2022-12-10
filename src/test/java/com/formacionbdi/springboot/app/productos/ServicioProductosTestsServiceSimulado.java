package com.formacionbdi.springboot.app.productos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.formacionbdi.springboot.app.commons.models.entity.Producto;
import com.formacionbdi.springboot.app.productos.models.dao.IProductoDao;
import com.formacionbdi.springboot.app.productos.models.service.ProductoServiceImpl;

@Tag("tests_simulado")
//2 FORMA PARA INYECTAR LAS CLASES RESPECTIVAMENTE: esto habilita laas anotaciones de inyeccion de los mocks (reemplaza la linea: MockitoAnnotations.openMocks(this)), 
//aunque lo comente y no genero error, probablemente por la version
//@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ServicioProductosTestsServiceSimulado {

	//2 FORMA PARA INYECTAR LAS CLASES RESPECTIVAMENTE
	@Mock
	IProductoDao iproductoDao; //TIENE QUE SER LLAMADO A LA INTERFAZ Y NO A LA IMPLEMENTACION
		
	//2 FORMA PARA INYECTAR LAS CLASES RESPECTIVAMENTE
	@InjectMocks
	ProductoServiceImpl serviceImpl; //TIENE QUE SER LLAMADO A LA IMPLEMENTACION Y NO A LA INTERFAZ
		
	@Test
	void contextLoadsServiceSimulado() {
		
		//1 FORMA PARA INYECTAR LAS CLASES RESPECTIVAMENTE
		//IProductoDao iproductoDao = mock(IProductoDao.class); //SE PUEDE INYECTAR LA INTERFAZ O IMPLEMENTACION
		//IProductoService service = new ProductoServiceImpl(iproductoDao); //INYECCION DE DEPENDENCIA POR CONSTRUCTOR

		Optional<Producto> producto = Optional.ofNullable(new Producto()); //Optional OBJETO QUE CONTIENE LA INSTANCIA EL OBJETO, CON MAS FUNCIONALIDADES
		producto.orElseThrow().setId(1L);		//Optional : orElseThrow: EXTRAE EL OBJETO Y EN CASO DE QUE SEA NULL GENERA EXCEPTION NoSuchElementException
		producto.orElseThrow().setNombre("pez");
		producto.orElseThrow().setPrecio(1000D);
		
		//when(iproductoDao.findById(1L)).thenReturn(producto);		 // se adiciona 1L que es el valor ingresado a probar
		when(iproductoDao.findById(anyLong())).thenReturn(producto); // se adiciona anyLong para que sea cualquier valor ingresado
		
		Producto productoDB = serviceImpl.findById(1);
		
		assertEquals("pez", productoDB.getNombre());
		
		verify(iproductoDao).findById(anyLong()); //verfica si hubo llamado del metodo mencionado durante el test de service.findById(1)
		//verify(iproductoDao).findAll();			//en este falla el test porque no llamo del metodo mencionado findAll()	
		
	}
	

}
