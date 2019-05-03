package br.com.cleiton.api.controllers;

import java.math.BigDecimal;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cleiton.api.dtos.CadastroPFDto;
import br.com.cleiton.api.entities.Empresa;
import br.com.cleiton.api.entities.Funcionario;
import br.com.cleiton.api.enums.PerfilEnum;
import br.com.cleiton.api.response.Response;
import br.com.cleiton.api.services.EmpresaService;
import br.com.cleiton.api.services.FuncionarioService;
import br.com.cleiton.api.utils.PasswordUtils;

@RestController
@CrossOrigin("*")
@RequestMapping("/pessoafisica")
public class CadastroPFController {

	@Autowired
	private FuncionarioService service;
	
	@Autowired
	private EmpresaService empresaService;
	
	@PostMapping("/cadastrar/")
	public ResponseEntity<Response<CadastroPFDto>> cadastrarPessoaFisica(@RequestBody @Valid CadastroPFDto dto,BindingResult result) {
		Response<CadastroPFDto> response = new Response<CadastroPFDto>();
		
		verifyIfPfExists(dto,result);
		if(result.hasErrors()) {
			result.getAllErrors().stream().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}		
		
		Optional<Empresa> empresa = this.empresaService.findCompanyByCnpj(dto.getCnpj());
		Funcionario funcionario = this.castDTOtoEntity(dto);
		funcionario.setEmpresa(empresa.get());
		service.saveFuncionario(funcionario);
		response.setData(this.castEntityToDto(funcionario));
		return ResponseEntity.ok(response);
	}
	
	private Funcionario castDTOtoEntity(CadastroPFDto dto) {
		Funcionario func = new Funcionario();
		func.setNome(dto.getNome());
		func.setEmail(dto.getEmail());
		func.setCpf(dto.getCpf());
		func.setSenha(PasswordUtils.gerarBCrypt(dto.getSenha()));
		func.setPerfil(PerfilEnum.ROLE_USUARIO);
		dto.getQtdHorasAlmoco().ifPresent(valor -> func.setQtdHorasAlmoco(Float.parseFloat(valor)));
		dto.getQtdHorasTrabalhoDia().ifPresent(valor -> func.setQtdHorasTrabalhoDia(Float.parseFloat(valor)));
		dto.getValorHora().ifPresent(valor -> func.setValorHora(new BigDecimal(valor)));
		return func;
	}
	
	private void verifyIfPfExists(CadastroPFDto dto,BindingResult result) {
		this.service.getFuncionarioByEmail(dto.getEmail()).ifPresent(funcionario -> result.addError(new ObjectError("funcionario","Email já existe!")));
		this.service.getFuncionarioByCpf(dto.getCpf()).ifPresent(funcionario -> result.addError(new ObjectError("funcionario","CPF já existe!")));;
		Optional<Empresa> emp = this.empresaService.findCompanyByCnpj(dto.getCnpj());
		if(!emp.isPresent()) {
			result.addError(new ObjectError("empresa","Empresa não encontrada!"));
		}
	}
	
	private CadastroPFDto castEntityToDto(Funcionario func) {
		CadastroPFDto dto = new CadastroPFDto();
		dto.setNome(func.getNome());
		dto.setEmail(func.getEmail());
		dto.setCpf(func.getCpf());
		dto.setSenha(func.getSenha());
		dto.setCnpj(func.getEmpresa().getCnpj());
		dto.setId(func.getId());
		func.getQtdHorasAlmocoOpt().ifPresent(value -> dto.setQtdHorasAlmoco(Optional.of(Float.toString(value))));
		func.getQtdHorasTrabalhoDiaOpt().ifPresent(value -> dto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(value))));
		func.getValorHoraOpt().ifPresent(value -> dto.setValorHora(Optional.of(value.toString())));
		return dto;
	}
	
	
}
