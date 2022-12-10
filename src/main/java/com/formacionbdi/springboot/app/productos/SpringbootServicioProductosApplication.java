package com.formacionbdi.springboot.app.productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.autoconfigure.domain.EntityScan;

//@EnableEurekaClient //por defecto no es necesario la anotacion, pero por buena practica es mejor adicionarla
//@EnableDiscoveryClient //por defecto no es necesario la anotacion, pero por buena practica es mejor adicionarla
@SpringBootApplication
@EntityScan({"com.formacionbdi.springboot.app.commons.models.entity"}) //este se define en donde se encuentra el package ya que la clase de Productos que es comun, se encuentra 
																		//en otro package, por fuera de esta la clase controladora SpringbootServicioProductosApplication
																		//quien es la encargada de que detecte y reconozca las clases: controladores, los models, repositorys, services, entitys 
public class SpringbootServicioProductosApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioProductosApplication.class, args);
	}

}
