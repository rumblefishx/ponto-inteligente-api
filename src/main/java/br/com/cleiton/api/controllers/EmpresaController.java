package br.com.cleiton.api.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cleiton.api.dtos.EmpresaDto;
import br.com.cleiton.api.entities.Empresa;
import br.com.cleiton.api.response.Response;
import br.com.cleiton.api.services.EmpresaService;

@RestController
@RequestMapping("/api/empresa")
@CrossOrigin("*")
public class EmpresaController {
	
	@Autowired
	private EmpresaService service;

	@GetMapping("/cnpj/{cnpj}")
	public ResponseEntity<Response<EmpresaDto>> cadastrarEmpresa(@PathVariable String cnpj) {
		Response<EmpresaDto> response = new Response<EmpresaDto>();
		Optional<Empresa> empresa = this.service.findCompanyByCnpj(cnpj);
	
		if(!empresa.isPresent()) {
			response.getErros().add("Nenhuma empresa foi localizada a partir do CNPJ " + cnpj);
			return ResponseEntity.badRequest().body(response);	
		}
				
		response.setData(this.castEntityToDto(empresa.get()));
		return ResponseEntity.ok(response);
	}
	
	
	public EmpresaDto castEntityToDto(Empresa entity) {
		EmpresaDto dto = new EmpresaDto();
		dto.setCnpj(entity.getCnpj());
		dto.setRazaoSocial(entity.getRazaoSocial());
		dto.setId(entity.getId());		
		return dto;
	}
	
}
