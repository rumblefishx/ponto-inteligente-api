package br.com.cleiton.api.controllers;

import org.slf4j.LoggerFactory;
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
@RequestMapping("/api")
@CrossOrigin("*")
/**
 * Classe de controller responsável pelo cadastro de Pessoa Juridica.
 * @author rumblefish
 *
 */
public class CadastroPJController {
	
	/**
	 * Classe de Logger
	 */
	private org.slf4j.Logger log = LoggerFactory.getLogger(CadastroPJController.class);
	
	/**
	 * Serviço de empresas
	 */
	@Autowired
	private EmpresaService empresaService;
	
	/**
	 * Serviço de funcionários.
	 */
	@Autowired
	private FuncionarioService funcionarioService;

	/**
	 * Método responsável por cadastrar uma pessoa juridica
	 * @param cadastro - DTO com os dados elegiveis para cadastro
	 * @param result - dados de validacao do objeto de entrada
	 * @return retorna os dados do PJ recem cadastrado.
	 */
	@PostMapping("/pessoa-juridica/")
	public ResponseEntity<Response<CadastroPJDto>> cadastrarPessoaJuridica(@Validated @RequestBody CadastroPJDto cadastro,BindingResult result){
		Response<CadastroPJDto> response = new Response<CadastroPJDto>();
		log.info("Iniciando cadastro de pessoa juridica!");
		
		this.validateRequest(cadastro, result);

		Empresa empresa = this.castDtoToEmpresa(cadastro);
		Funcionario funcionario = this.castDtoToFuncionario(cadastro);
		
		if(result.hasErrors()) {
			result.getAllErrors().stream().forEach(error -> result.addError(error));
			log.error("Cadastro interrompido em razão de falhas");
			return ResponseEntity.badRequest().body(response);
		}
		
		this.empresaService.saveCompany(empresa);	
		funcionario.setEmpresa(empresa);
		this.funcionarioService.saveFuncionario(funcionario);
		response.setData(this.castFuncionarioToResponseDTO(funcionario));
		log.info("finalizando cadastro de pessoa juridica!");
		return ResponseEntity.ok(response);
	}		
	
	/**
	 * Método responsável por validar se o cadastro ja existe na base.
	 * @param cadastro - Dados do cadastro
	 * @param result - validacao dos dados de entrada.
	 */
	private void validateRequest(CadastroPJDto cadastro,BindingResult result) {
		empresaService.findCompanyByCnpj(cadastro.getCnpj()).ifPresent(emp -> result.addError(new ObjectError("empresa","Empresa ja existe!")));
		funcionarioService.getFuncionarioByEmail(cadastro.getEmail()).ifPresent(func -> result.addError(new ObjectError("funcionario","Email já existe!")));
		funcionarioService.getFuncionarioByCpf(cadastro.getCpf()).ifPresent(func -> result.addError(new ObjectError("funcionario","CPF já existe!")));
	}
	
	/**
	 * Converte um DTO de entrada para a Entity de Empresa
	 * @param dto - Dto com os dados submetidos para o controller
	 * @return entity Empresa
	 */
	private Empresa castDtoToEmpresa(CadastroPJDto dto) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(dto.getCnpj());
		empresa.setRazaoSocial(dto.getRazaoSocial());
		return empresa;
	}
	
	/**
	 * Converte um Dto de Funcionario para a respectiva Entity Funcionario
	 * @param dto dados submetidos ao controller 
	 * @return retorna uma entity Funcionario
	 */
	private Funcionario castDtoToFuncionario(CadastroPJDto dto) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(dto.getNome());
		funcionario.setCpf(dto.getCpf());
		funcionario.setEmail(dto.getEmail());
		funcionario.setSenha(PasswordUtils.gerarBCrypt(dto.getSenha()));
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		return funcionario;
	}
	
	/**
	 * Método responsável por converter uma entity de funcionario em seu respectivo DTO
	 * @param funcionario - Entity Funcionario
	 * @return retorna um objeto do tipo CadastroPJDto
	 */
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
