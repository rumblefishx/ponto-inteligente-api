package br.com.cleiton.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cleiton.api.dtos.CadastroPJDto;
import br.com.cleiton.api.entities.Empresa;
import br.com.cleiton.api.entities.Funcionario;
import br.com.cleiton.api.enums.PerfilEnum;
import br.com.cleiton.api.response.Response;
import br.com.cleiton.api.services.EmpresaService;
import br.com.cleiton.api.services.FuncionarioService;
import br.com.cleiton.api.utils.PasswordUtils;

@RestController
@RequestMapping("/pessoajuridica")
@CrossOrigin("*")
public class CadastroPJController {
	
	@Autowired
	private EmpresaService empresaService;
	
	@Autowired
	private FuncionarioService funcionarioService;

	
	@PostMapping("/cadastrar/")
	public ResponseEntity<Response<CadastroPJDto>> cadastrarPessoaJuridica(@Validated @RequestBody CadastroPJDto cadastro,BindingResult result){
		Response<CadastroPJDto> response = new Response<CadastroPJDto>();
		
		this.validateRequest(cadastro, result);

		Empresa empresa = this.castDtoToEmpresa(cadastro);
		Funcionario funcionario = this.castDtoToFuncionario(cadastro);
		
		if(result.hasErrors()) {
			result.getAllErrors().stream().forEach(error -> result.addError(error));
			return ResponseEntity.badRequest().body(response);
		}
		
		this.empresaService.saveCompany(empresa);	
		funcionario.setEmpresa(empresa);
		this.funcionarioService.saveFuncionario(funcionario);
		response.setData(this.castFuncionarioToResponseDTO(funcionario));
		return ResponseEntity.ok(response);
	}		
	
	private void validateRequest(CadastroPJDto cadastro,BindingResult result) {
		empresaService.findCompanyByCnpj(cadastro.getCnpj()).ifPresent(emp -> result.addError(new ObjectError("empresa","Empresa ja existe!")));
		funcionarioService.getFuncionarioByEmail(cadastro.getEmail()).ifPresent(func -> result.addError(new ObjectError("funcionario","Email já existe!")));
		funcionarioService.getFuncionarioByCpf(cadastro.getCpf()).ifPresent(func -> result.addError(new ObjectError("funcionario","CPF já existe!")));
	}
	
	private Empresa castDtoToEmpresa(CadastroPJDto dto) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(dto.getCnpj());
		empresa.setRazaoSocial(dto.getRazaoSocial());
		return empresa;
	}
	
	private Funcionario castDtoToFuncionario(CadastroPJDto dto) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(dto.getNome());
		funcionario.setCpf(dto.getCpf());
		funcionario.setEmail(dto.getEmail());
		funcionario.setSenha(PasswordUtils.gerarBCrypt(dto.getSenha()));
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		return funcionario;
	}
	
	private CadastroPJDto castFuncionarioToResponseDTO(Funcionario funcionario) {
		CadastroPJDto dto = new CadastroPJDto();
		dto.setNome(funcionario.getNome());
		dto.setEmail(funcionario.getEmail());
		dto.setCpf(funcionario.getCpf());
		dto.setCnpj(funcionario.getEmpresa().getCnpj());
		dto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
		return dto;
	}
	
}
