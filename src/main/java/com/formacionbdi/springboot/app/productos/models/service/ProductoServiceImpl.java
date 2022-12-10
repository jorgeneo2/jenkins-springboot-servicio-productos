package com.formacionbdi.springboot.app.productos.models.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formacionbdi.springboot.app.commons.models.dto.ReporteDTO;
import com.formacionbdi.springboot.app.commons.models.entity.Producto;
import com.formacionbdi.springboot.app.productos.models.dao.IProductoDao;
import com.google.gson.Gson;

import io.micrometer.core.instrument.util.StringEscapeUtils;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.util.JRSaver;
import utils.LZString;

@Service // para que se registre en el contenedor y se puede usar despues
public class ProductoServiceImpl implements IProductoService {

	//1 FORMA: INYECCION POR VARIABLE DE INSTANCIA
	@Autowired
	private IProductoDao iproductoDao;

	/**2 FORMA: INYECCION POR CONSTRUCTOR
	 * @param iproductoDao
	 */
	/*@Autowired
	public ProductoServiceImpl(IProductoDao iproductoDao) {
		super();
		this.iproductoDao = iproductoDao;
	}*/
	
	@Value("${path.reportes}")
	private String pathReportes;
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductoServiceImpl.class);

	@Override
	@Transactional(readOnly = true) // aunque es de consulta, es mejor para mantengan los datos sincronizados con la
									// BD
	public List<Producto> findAll() {
		return (List<Producto>) iproductoDao.findAll();
	}
	
	@Override
	public Page<Producto> findAll(Pageable pageable) {
		return iproductoDao.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Producto findById(long id) {
		return iproductoDao.findById(id).orElse(null);
	}
	
	@Override
	@Transactional
	public Producto save(Producto producto) {
		return iproductoDao.save(producto);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		iproductoDao.deleteById(id);
	}
	
	public ReporteDTO exportReport(String reportFormat, String dataType) throws Exception {
        
		ReporteDTO reporteDTO = new ReporteDTO();
		
		try {
			
			String base64Content;
			byte[] output = null;
	        List<Producto> productos = (List<Producto>) iproductoDao.findAll();
	        
	        	        
	        //1.1 **** load file 
	        //File file = ResourceUtils.getFile("classpath:reporteProductos_con_Integer.jrxml");
	        //1.2 **** load file 	        
	        //File file = ResourceUtils.getFile("classpath:reporteProductos_con_String.jrxml");
	        //1.3 **** load file 
	        File file = ResourceUtils.getFile("classpath:reporteProductos.jrxml");
	        
	        
	        //compile it
	        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
	        //save object para manejo de subreportes: No funciona por el momento 
	        JRSaver.saveObject(jasperReport, "reporteProductos.jasper");
	        //load data
	        JRBeanCollectionDataSource dataSourceObject = new JRBeanCollectionDataSource(productos);	        
	        JRBeanCollectionDataSource dataSourceParamListaProductosTableReport = new JRBeanCollectionDataSource(productos);
	        
	        //load file
			File file2 = ResourceUtils.getFile("classpath:subReporteProductos.jrxml");
			//compile it
			JasperReport jasperReport2 = JasperCompileManager.compileReport(file2.getAbsolutePath());
			//save object para manejo de subreportes: No funciona por el momento
			JRSaver.saveObject(jasperReport2, "subReporteProductos.jasper");
			 
	        //INICIO DATASOURCE CON BASE EN UN STRING 
			//<OBTENIENDO UNA LISTA DE UN OBJETO EN PARTICULAR, SE CONVIERTE EN UN STRING 
			//Y SE PASA A UN ObjectMapper PARA GENERAR UN Collection<?> REQUERIDO PARA EL JRBeanCollectionDataSource>
        	
			Producto producto = new Producto();
	        producto.setId(Long.valueOf(11));
	        producto.setNombre("valor campo 11");
	        producto.setPrecio(Double.valueOf(10000));
	        
	        Producto producto2 = new Producto();
	        producto2.setId(Long.valueOf(12));
	        producto2.setNombre("valor campo 12");
	        producto2.setPrecio(Double.valueOf(20000));
	        
	        Gson gson = new Gson();
	        Producto productoJson = new Producto();
	        List<Producto> listaProductosJson = new ArrayList<Producto>();
	        
	        productoJson.setId(Long.valueOf(33));
	        productoJson.setNombre("valor campo 33");
	        productoJson.setPrecio(Double.valueOf(30000));
	        
	        listaProductosJson.add(producto);
	        listaProductosJson.add(producto2);
	        productoJson.setListaProductos(listaProductosJson);
	        			
	        //2.1 **** Usar el reporte: reporteProductos_con_Integer.jrxml
	        String objetoListJson = gson.toJson(productoJson.getListaProductos(), List.class);
	        LOG.info("String objetoJson: {}",objetoListJson);
	        //LOG.info("escapeJson: {}",StringEscapeUtils.escapeJson(objetoListJson));
	        	   
	        	//2.2 **** Usar el reporte: reporteProductos_con_String.jrxml
	        	//data quemada correcta: Los campos en el jasper en el reporteProductos.jrxml en los field tienen que ser String para que haga el cast correctamente
	        	//, para el ejemplo id: String / precio: String / port: String
			//objetoListJson = "[{\"id\":\"valor campo 1\",\"nombre\":\"valor campo 2\",\"precio\":\"valor campo 3\"},{\"id\":\"valor campo 11\",\"nombre\":\"valor campo 22\",\"precio\":\"valor campo 33\"}]";
	        
	        	//2.1 **** Usar el reporte: reporteProductos_con_Integer.jrxml
	        	//Los campos Long jasper los interpreta como Integer, por lo que toca hacer el cambio en el reporteProductos.jrxml en los field para que haga el cast correctamente
	        	//, para el ejemplo id: Integer / precio: Double / port: Integer
	        //objetoListJson = "[{\"id\":"+Long.valueOf(11)+",\"nombre\":\"valor campo 2\",\"precio\":"+Double.valueOf(10000)+"},{\"id\":"+Long.valueOf(22)+",\"nombre\":\"valor campo 22\",\"precio\":"+Double.valueOf(20000)+"}]";
			
			 LOG.info("String objetoJson: {}",objetoListJson);
							
			ObjectMapper objectMapper = new ObjectMapper();
			List<Object> items = objectMapper.readValue(
					objetoListJson,
				    objectMapper.getTypeFactory().constructParametricType(List.class, Object.class)
				);
			
			LOG.info("items: {}",items.toString());		
			
			//load data
			JRBeanCollectionDataSource dataSourceJson = new JRBeanCollectionDataSource(items);
			
			//FIN DATASOURCE CON BASE EN UN STRING 
		        
			
			//SE PASAN LOS PARAMETROS Y LA DATA
			Map<String, Object> parameters = new HashMap<>();
	        parameters.put("createdBy", "Java George");
	        parameters.put("paramListaProductos", dataSourceParamListaProductosTableReport);	
	        
	        JasperPrint jasperPrint = null;
	        
	        if(dataType.equalsIgnoreCase("json")) {
		        //create report dataSourceJson
		        //3.1 **** Usar el reporte: reporteProductos_con_Integer.jrxml
		        //3.2 **** Usar el reporte: reporteProductos_con_String.jrxml
		        //JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSourceJson);
		        
		        //create report dataSourceObject
		        //3.3 **** Usar el reporte: reporteProductos.jrxml
		        //Como viene de un objeto el campo id(Long) jasper los interpreta como Long, por lo que toca hacer el cambio en el reporte reporteProductos.jrxml en los field para que haga el cast correctamente
		        //, para el ejemplo id: Long / precio: Double / port: Integer
		        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSourceObject);
	        
	        }
	        if(dataType.equalsIgnoreCase("csv")) {
		        //INICIO: REPORTE CON DATA CSV
		        //load file 
		        File fileCsv = ResourceUtils.getFile("classpath:miRreporteConDataCsv.jrxml");
		        
		        
		        //compile it
		        JasperReport jasperReportCsv = JasperCompileManager.compileReport(fileCsv.getAbsolutePath());
		        
		        //load data	        
		        JRCsvDataSource dataSourceCsv = new JRCsvDataSource("classpath:data-csv.csv");	
		        dataSourceCsv.setUseFirstRowAsHeader(true);
		        
		        jasperPrint = JasperFillManager.fillReport(jasperReportCsv, parameters, dataSourceCsv);
		        //FIN: REPORTE CON DATA CSV
	        }
	        
	        if (reportFormat.equalsIgnoreCase("html")) {
	        	JasperExportManager.exportReportToHtmlFile(jasperPrint, pathReportes + "productos.html");
	        	output = JasperExportManager.exportReportToXml(jasperPrint).getBytes();
	        }
	        if (reportFormat.equalsIgnoreCase("pdf")) {
	        	JasperExportManager.exportReportToPdfFile(jasperPrint, pathReportes+"productos.pdf");
	        	output = JasperExportManager.exportReportToPdf(jasperPrint);
	        }
	        
	        base64Content = Base64.getEncoder().encodeToString(output);
	        
	        reporteDTO.setMensaje("report generated in path : " + pathReportes);
	        reporteDTO.setBase64Content(base64Content);
	        	        	      
        
		}catch(Exception e) {
			
			LOG.error("Error exportReport e.getMessage: {}",e.getMessage());
			Arrays.asList(e.getStackTrace()).stream().limit(4).forEach(s -> 
			LOG.error(String.format("\t at %s%s.%s(%s)",
                        (s.getFileName() == null) ? "" : String.format("%s/", s.getFileName()),
                        s.getClassName(),
                        s.getMethodName(),
                        (s.isNativeMethod() ? "Native Method" : String.format("%s:%d", s.getFileName(), s.getLineNumber())))));
			
			LOG.info("Error exportReport: {}",e);

			throw new Exception(e);
		}
		return reporteDTO;
    }
	
public ReporteDTO exportReportConDataString(String reportFormat, String dataType) throws Exception {
        
		ReporteDTO reporteDTO = new ReporteDTO();
		
		try {
			
			String base64Content;
	        
			File file = null;
			String dataReport ="";     
			
			if(dataType.equalsIgnoreCase("json")) {
				//2.2 **** Usar el reporte: reporteProductos_con_String.jrxml
	        	//data quemada correcta: Los campos en el jasper en el reporteProductos.jrxml en los field tienen que ser String para que haga el cast correctamente
	        	//, para el ejemplo id: String / precio: String / port: String
				dataReport = "[{\"id\":\"valor campo 1\",\"nombre\":\"valor campo 2\",\"precio\":\"valor campo 3\"},{\"id\":\"valor campo 11\",\"nombre\":\"valor campo 22\",\"precio\":\"valor campo 33\"}]";
	        
	        	//2.1 **** Usar el reporte: reporteProductos_con_Integer.jrxml
	        	//Los campos Long jasper los interpreta como Integer, por lo que toca hacer el cambio en el reporteProductos.jrxml en los field para que haga el cast correctamente
	        	//, para el ejemplo id: Integer / precio: Double / port: Integer
				//objetoListJson = "[{\"id\":"+Long.valueOf(11)+",\"nombre\":\"valor campo 2\",\"precio\":"+Double.valueOf(10000)+"},{\"id\":"+Long.valueOf(22)+",\"nombre\":\"valor campo 22\",\"precio\":"+Double.valueOf(20000)+"}]";
			
				
		        //1.1 **** load file 
		        //File file = ResourceUtils.getFile("classpath:reporteProductos_con_Integer.jrxml");
		        //1.2 **** load file 	        
		        file = ResourceUtils.getFile("classpath:reporteProductos_con_String.jrxml");
		        
			}
			
			if(dataType.equalsIgnoreCase("csv")) {
				file = ResourceUtils.getFile("classpath:miRreporteConDataCsv.jrxml");
				dataReport = "Nombre,Tipo de Identificación,Identificación,Tipo de Pago,Código de Dependencia,Tipo de Cuenta,Número de Cuenta,Código Banco,Banco,Cuenta Por Defecto,Correo Electrónico,Notificación Correo,Fecha de Creación,Fecha de Modificación,Estado Registro,Motivo Rechazo,Mensaje\nINSTITUTO PER,Nit,456789,credito,2,AHO,987,BC2,mi bank,AHO 987,ma@itau.co,N,01-02-2022 / 13:00,02-02-2022 / 16:00,OK,\"No se rechaza, todo se encuentra correcto\",1 valor nombre 2 valor";
			}
	        
			LOG.info("String dataReport: {}",dataReport);
	        
			
			 String stringParameters="createdBy:Java George,otroParametro:valor del parametro";
			 
			 base64Content = creacionJasper(reportFormat, dataType, file.getAbsolutePath(), stringParameters, dataReport); 
				        
	        
	        reporteDTO.setMensaje("report generated in path : " + pathReportes);
	        reporteDTO.setBase64Content(base64Content);
	        	        
	        
		}catch(Exception e) {
			LOG.info("Error exportReport: {}",e);
			throw new Exception(e);
		}
		return reporteDTO;
    }
	

public ReporteDTO exportReportConDataBase64(ReporteDTO reporteResponseDTO) throws Exception {
    
	ReporteDTO reporteDTO = new ReporteDTO();
	
	try {
		
		String base64Content;
		String stringParametersResponse=reporteResponseDTO.getStringParameters();
        		
		File file = ResourceUtils.getFile("classpath:"+reporteResponseDTO.getNameJrxml()+".jrxml");
		String dataReportResponse = "";
			
				if(reporteResponseDTO.isComprimido()) {
					if(reporteResponseDTO.isBase64()) {
						reporteResponseDTO.setBase64Content(descomprimir(reporteResponseDTO.getBase64Content()));
					}else {
						reporteResponseDTO.setDataReport(descomprimir(reporteResponseDTO.getDataReport()));
					}
				}
		dataReportResponse = reporteResponseDTO.isBase64()?decodificarBase64(reporteResponseDTO.getBase64Content()):reporteResponseDTO.getDataReport();  
        
		LOG.info("reportFormat: {}",reporteResponseDTO.getReportFormat());
		LOG.info("dataType: {}",reporteResponseDTO.getDataType());
		LOG.info("file.getAbsolutePath(): {}",file.getAbsolutePath());
		LOG.info("stringParametersResponse: {}",stringParametersResponse);
		//LOG.info("dataReportResponse: {}",dataReportResponse);
		
		base64Content = creacionJasper( reporteResponseDTO.getReportFormat(), reporteResponseDTO.getDataType(), file.getAbsolutePath(), reporteResponseDTO.getStringParameters(), dataReportResponse);
				
        reporteDTO.setMensaje("report generated in path : " + pathReportes);
        reporteDTO.setBase64Content(base64Content);
        	        
        
	}catch(Exception e) {
		LOG.info("Error exportReport: {}",e);
		throw new Exception(e);
	}
	return reporteDTO;
}

public String decodificarBase64(String content) {
	return new String(Base64.getDecoder().decode(content.getBytes()));
}

public String codificarBase64(byte[] content) {
	return new String(Base64.getEncoder().encodeToString(content));
}

public String codificarBase64(String content) {
	return new String(Base64.getEncoder().encodeToString(content.getBytes()));
}

//uso clase LZString.java y test prueba-aws-lambda-node\\lambdas\\indexTest.js
public String descomprimir(String content) {
	

	LOG.info("comprimido tamano {}", content.length());
	//LOG.info("comprimido: {}",content);

	String decompressed = LZString.decompress(content);
	
	LOG.info("descomprimido tamano {}", decompressed.length());
	//LOG.info("descomprimido: {}",decompressed);
	
	return decompressed;
}

//uso clase LZString.java y test prueba-aws-lambda-node\\lambdas\\indexTest.js
public String comprimir(String content) {
	
	LOG.info("descomprimido tamano {}", content.length());
	//LOG.info("descomprimido: {}",content);
	
	
	String compressed = LZString.compress(content);

	LOG.info("comprimido tamano {}", compressed.length());
	//LOG.info("comprimido: {}",compressed);
	
	return compressed;
}

	public String creacionJasper(String reportFormat, String dataType, String pathJrxml, String stringParameters, String dataReport) {
		
		  
        JasperPrint jasperPrint = null;

		byte[] output = null;
        
		try {
			
			//convert string to hashmap
			Map<String, Object> parameters=new HashMap<String, Object>();
			StringTokenizer st2=new StringTokenizer(stringParameters, ",");
		    while(st2.hasMoreTokens())
		    {
		        String [] array=st2.nextToken().split(":");
		        parameters.put(array[0], array[1]);
		    }
						
			//compile it
	        JasperReport jasperReport = JasperCompileManager.compileReport(pathJrxml);
	        
	        if(dataType.equalsIgnoreCase("json")) {
	        	//convert string to collection object
				ObjectMapper objectMapper = new ObjectMapper();
				List<Object> listObject = objectMapper.readValue(
						dataReport,
					    objectMapper.getTypeFactory().constructParametricType(List.class, Object.class)
					);
		        //load data
		        JRBeanCollectionDataSource dataSourceObject = new JRBeanCollectionDataSource(listObject);	
		        
		        //create report
		        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSourceObject);
	        }
	        
	        if(dataType.equalsIgnoreCase("csv")) {		
	        	//convert string to InputStream
				InputStream dataStream = new ByteArrayInputStream(dataReport.getBytes());
		        //load data	
		        JRCsvDataSource dataSourceCsv = new JRCsvDataSource(dataStream);	
		        dataSourceCsv.setUseFirstRowAsHeader(true);
		        
		        //create report
		        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSourceCsv);
		      
	        }
	        
	        if (reportFormat.equalsIgnoreCase("html")) {
	        	JasperExportManager.exportReportToHtmlFile(jasperPrint, pathReportes + "productos.html");
	        	output = JasperExportManager.exportReportToXml(jasperPrint).getBytes();
	        }
	        if (reportFormat.equalsIgnoreCase("pdf")) {
	        	JasperExportManager.exportReportToPdfFile(jasperPrint, pathReportes+"productos.pdf");
	        	output = JasperExportManager.exportReportToPdf(jasperPrint);
	        }
	        	        
	      
		}catch(Exception e) {
			LOG.info("Error creacionJasper: {}",e);
		}
		
		return codificarBase64(output);
	}

}
