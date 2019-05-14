package br.com.cleiton.api.controllers;

import java.math.BigDecimal;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cleiton.api.dtos.FuncionarioDto;
import br.com.cleiton.api.entities.Funcionario;
import br.com.cleiton.api.response.Response;
import br.com.cleiton.api.services.FuncionarioService;
import br.com.cleiton.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/funcionario")
@CrossOrigin("*")
public class FuncionarioController {

	@Autowired
	private FuncionarioService service;
	
	@PutMapping("/{id}")
	public ResponseEntity<Response<FuncionarioDto>> atualizarFuncionario(@PathVariable Long id,
			@RequestBody @Valid FuncionarioDto dto,BindingResult result) {
		Response<FuncionarioDto> response = new Response<FuncionarioDto>();
		Optional<Funcionario> entity = this.service.getFuncionarioById(id);
		
		if(!entity.isPresent()) {
			result.addError(new ObjectError("Funcionario", "Funcionário não encontrado por meio do ID informado."));
		}
		
		this.updateEntityFromDto(result,entity.get(), dto);
		
		if(result.hasErrors()) {
			result.getAllErrors().forEach(erro -> response.getErros().add(erro.getDefaultMessage()));
			response.setData(dto);
			ResponseEntity.badRequest().body(response);
		}
		
		this.service.saveFuncionario(entity.get());
		response.setData(this.castEntityToDto(entity.get()));
		return ResponseEntity.ok(response);
	}
	
	private FuncionarioDto castEntityToDto(Funcionario funcionario) {
		FuncionarioDto dto = new FuncionarioDto();
		dto.setNome(funcionario.getNome());
		dto.setEmail(funcionario.getEmail());
		dto.setId(funcionario.getId());
		if(funcionario.getQtdHorasAlmocoOpt().isPresent())
			dto.setQtdHorasAlmoco(Optional.of(funcionario.getQtdHorasAlmoco().toString()));
		
		if(funcionario.getQtdHorasTrabalhoDiaOpt().isPresent())
			dto.setQtdHorasTrabalhoDia(Optional.of(funcionario.getQtdHorasTrabalhoDia().toString()));
		
		if(funcionario.getValorHoraOpt().isPresent())
			dto.setValorHora(Optional.of(funcionario.getValorHora().toString()));
		
		dto.setSenha(null);
		return dto;
	}

	private void updateEntityFromDto(BindingResult result,Funcionario entity,FuncionarioDto dto) {
		
		if(!dto.getEmail().equals(entity.getEmail())) {
			this.service.getFuncionarioByEmail(dto.getEmail())
			.ifPresent(funcionario -> result.addError(new ObjectError("Funcionario", "O e-mail informado já existe!")));
			entity.setEmail(dto.getEmail());
		}
		
		entity.setNome(dto.getNome());
		
		entity.setValorHora(null);
		dto.getValorHora().ifPresent(valorHora -> entity.setValorHora(new BigDecimal(valorHora)));
		
		entity.setQtdHorasTrabalhoDia(null);
		dto.getQtdHorasTrabalhoDia().ifPresent(qtdHoras -> entity.setQtdHorasTrabalhoDia(Float.parseFloat(qtdHoras)));
		
		entity.setQtdHorasAlmoco(null);
		dto.getQtdHorasAlmoco().ifPresent(qtdHorasAlmoco -> entity.setQtdHorasAlmoco(Float.parseFloat(qtdHorasAlmoco)));
		
		dto.getSenha().ifPresent(senha-> entity.setSenha(PasswordUtils.gerarBCrypt(senha)));	
		
	}

}
