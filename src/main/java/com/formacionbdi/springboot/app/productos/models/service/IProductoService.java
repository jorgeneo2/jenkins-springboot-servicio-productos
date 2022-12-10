package com.formacionbdi.springboot.app.productos.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.formacionbdi.springboot.app.commons.models.dto.ReporteDTO;
import com.formacionbdi.springboot.app.commons.models.entity.Producto;


public interface IProductoService {

	public List<Producto> findAll();
	
	public Page<Producto> findAll(Pageable pageable);

	public Producto findById(long id);
	
	public Producto save(Producto producto);
	
	public void deleteById(Long id);
	
	public ReporteDTO exportReport(String reportFormat, String dataType) throws Exception;
	
	public ReporteDTO exportReportConDataString(String reportFormat, String dataType) throws Exception;
	
	public ReporteDTO exportReportConDataBase64(ReporteDTO reporteResponseDTO) throws Exception;

}
