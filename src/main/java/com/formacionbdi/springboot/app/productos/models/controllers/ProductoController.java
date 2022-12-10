package com.formacionbdi.springboot.app.productos.models.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/*import java.util.logging.Level;
import java.util.logging.Logger;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.formacionbdi.springboot.app.commons.models.dto.ReporteDTO;
import com.formacionbdi.springboot.app.commons.models.entity.Producto;
import com.formacionbdi.springboot.app.productos.models.service.IProductoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

@CrossOrigin
@Api(tags = "servicio-productos", description = "Servicio REST para los productos") //anotacion de swagger documentacion
@RestController // esta anotacion convierte a json los metodos handler del controlador
public class ProductoController {
	
	//1 FORMA PARA LOGS
	//private static final Logger LOG = Logger.getLogger(ProductoController.class.getName());
	
	//2 FORMA PARA LOGS
	private static final Logger LOG = LoggerFactory.getLogger(ProductoController.class);

	@Autowired
	private Environment env; //1 FORMA CARGA PROPERTIES: Permite cargar el aplication.properties
	
	@Value("${server.port}") //2 FORMA CARGA PROPERTIES: Permite cargar el aplication.properties
	private Integer port;
	
	@Autowired
	private IProductoService iproductoService;

	@GetMapping("/listar")	
	public List<Producto> listar( 
			@ApiParam(value="header obligatorio", example="algun valor myHeader", required = true, defaultValue = "no envio header") 
			@RequestHeader(required = true) HashMap<String, String> myHeader) { //Como tiene la anotacion @RequestHeader, no es obligatorio pasarle el parametro de headers
		
		LOG.info("myHeader Request listar{}",myHeader);
		
		String response = "*** Welcome to listar servicio productos *** " + new Date();
		//1 FORMA PARA LOGS
		//LOG.log(Level.INFO, response);
		
		//2 FORMA PARA LOGS
		LOG.info("response: {}",response);
		
		return iproductoService.findAll().stream().map(producto -> {
			//producto.setPort(Integer.parseInt(env.getProperty("local.server.port"))); //1 FORMA CARGA PROPERTIES
			producto.setPort(port); //2 FORMA CARGA PROPERTIES
			return producto;
		}).collect(Collectors.toList());
	}
	
	@GetMapping("/paginador/page/{page}/cantidadPorPage/{cantidadPorPage}")
	public Page<Producto> paginador(@PathVariable Integer page, @PathVariable Integer cantidadPorPage) throws Exception{
		
		Pageable pageable = PageRequest.of(page, cantidadPorPage);
		
		return iproductoService.findAll(pageable);
		
	}

	@GetMapping("/ver/{id}")
	public ResponseEntity<?> /*Producto*/ detalle( 
			@ApiParam(value="header obligatorio", example="algun valor myHeader", required = true, defaultValue = "no envio header") 
			@RequestHeader HashMap<String, String> myHeader, @PathVariable Long id, 
			@ApiParam(value="header obligatorio", example="algun valor otroHeader", required = true) 
			@RequestHeader(name="request-name-unHeaderMas", required = true)  String otroHeader) throws Exception { //se tiene que poner el la propiedad name en caso de que se envia la peticion tambien con la propiedad name @RequestHeader(name=
		Producto producto = iproductoService.findById(id);
		//producto.setPort(Integer.parseInt(env.getProperty("local.server.port"))); //1 FORMA CARGA PROPERTIES
		producto.setPort(port); //2 FORMA CARGA PROPERTIES
		
		LOG.info("myHeader Request detalle {}",myHeader); //el header que viene en el parametro de entrada otroHeader, tambien se refleja en este myHeader, por lo tanto se puede quitar de la declaracion del metodo
		LOG.info("otroHeader Request detalle {}",otroHeader);
		
		//simulacion error para hystrix
		/*boolean ok = false;		
		if(ok == false) {
			throw new Exception("No se puede cargar el producto");
		}*/
		
		//simulacion error por timeout para hystrix
		/*try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("response-algun-head", "valor con MultiValueMap");
		headers.add("response-otro-head", "otro valor con MultiValueMap");
		
		HttpHeaders headers2 = new HttpHeaders();
	    headers2.add("response-algun-head", "valor con HttpHeaders");
		
		return new ResponseEntity<Producto>(producto, headers,HttpStatus.OK);
		
		//return producto; //EN CASO DE RETORNAR SOLO EL OBJETO
	}
	
	@PostMapping("/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public Producto crear(@RequestBody Producto producto) {
		return iproductoService.save(producto);
		
	}
	
	@PutMapping("/editar/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Producto editar(@RequestBody Producto producto, @PathVariable Long id) {
		Producto productoDb = iproductoService.findById(id);
		
		productoDb.setNombre(producto.getNombre());
        productoDb.setPrecio(producto.getPrecio());
        
        return iproductoService.save(productoDb);
	}
	
	@DeleteMapping("/eliminar/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable Long id) {
		iproductoService.deleteById(id);
	}
	
	@GetMapping("/report/{format}/{dataType}")
    public ResponseEntity<?> generateReport(
    		@ApiParam(value="valor formato obligatorio pdf o html", example="pdf", required = true) @PathVariable(required = true) String format,
    		@ApiParam(value="valor tipo de data obligatorio json o csv", example="json", required = true) @PathVariable(required = true) String dataType) throws Exception {
        
		HttpHeaders headers = new HttpHeaders();
	    headers.add("content-type", "application/json");	    // application/octet-stream: en swagger lo baja como adjunto, respondiendo con reporteDTO.getBase64Content()  
	    														// application/json respondiendo con reporteDTO
	    ReporteDTO reporteDTO = iproductoService.exportReport(format, dataType);
		
		return new ResponseEntity<>(reporteDTO, headers,HttpStatus.OK);
    }
	
	@GetMapping("/reportConDataString/{format}/{dataType}")
    public ResponseEntity<?> generateReportConDataString(
    		@ApiParam(value="valor formato obligatorio pdf o html", example="pdf", required = true) @PathVariable(required = true) String format,
    		@ApiParam(value="valor tipo de data obligatorio json o csv", example="json", required = true) @PathVariable(required = true) String dataType) throws Exception {
        
		HttpHeaders headers = new HttpHeaders();
	    headers.add("content-type", "application/json");	    // application/octet-stream: en swagger lo baja como adjunto, respondiendo con reporteDTO.getBase64Content()  
	    														// application/json respondiendo con reporteDTO
	    ReporteDTO reporteDTO = iproductoService.exportReportConDataString(format, dataType);
		
		return new ResponseEntity<>(reporteDTO, headers,HttpStatus.OK);
    }
	

	@PostMapping("/reportConDataBase64")
	public ResponseEntity<?> generateReportConDataBase64(@RequestBody ReporteDTO reporteResponseDTO) throws Exception{
         
		HttpHeaders headers = new HttpHeaders();
	    headers.add("content-type", "application/json");	    // application/octet-stream: en swagger lo baja como adjunto, respondiendo con reporteDTO.getBase64Content()  
	    														// application/json respondiendo con reporteDTO
	    ReporteDTO reporteDTO = iproductoService.exportReportConDataBase64(reporteResponseDTO);
		
		return new ResponseEntity<>(reporteDTO, headers,HttpStatus.OK);
    }

}
