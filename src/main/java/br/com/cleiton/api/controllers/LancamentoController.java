package br.com.cleiton.api.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.cleiton.api.dtos.LancamentoDto;
import br.com.cleiton.api.entities.Funcionario;
import br.com.cleiton.api.entities.Lancamento;
import br.com.cleiton.api.enums.TipoEnum;
import br.com.cleiton.api.response.Response;
import br.com.cleiton.api.services.FuncionarioService;
import br.com.cleiton.api.services.LancamentoService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/lancamento")
public class LancamentoController {

	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Value("${consulta.lancamentos.numeropaginas}")
	private Integer pageSize;
	
	@GetMapping("/id/{id}")
	public ResponseEntity<Response<LancamentoDto>> buscarLancamentoPorId(@PathVariable Long id) {
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorLancamentoId(id);
		
		if(!lancamento.isPresent()) {
			response.getErros().add("Lancamento não encontrado!");
			return ResponseEntity.badRequest().body(response);
		}
		
		LancamentoDto dto = this.castEntityToDto(lancamento.get());
		response.setData(dto);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/funcionario/{funcionarioId}")
	public ResponseEntity<Response<Page<LancamentoDto>>> buscarLancamentos(@PathVariable Long funcionarioId,
			@RequestParam(name="pag",defaultValue="0") int pag,
			@RequestParam(name="ord",defaultValue="id") String ord,
			@RequestParam(name="dir",defaultValue="DESC") String dir) {
		
		Response<Page<LancamentoDto>> response = new Response<Page<LancamentoDto>>();
		
		PageRequest pr = new PageRequest(pag, pageSize,Direction.valueOf(dir), ord);
		Page<Lancamento> lancamentos = this.lancamentoService.buscarPorFuncionarioId(funcionarioId, pr);
		Page<LancamentoDto> lancamentosDto = lancamentos.map(lanc -> this.castEntityToDto(lanc));
		
		response.setData(lancamentosDto);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/remover/{id}")
	public ResponseEntity<Response<String>> removerLancamento(@PathVariable Long id) {
		Response<String> response = new Response<String>();
		
		if(!this.lancamentoService.buscarPorLancamentoId(id).isPresent()) {
			response.getErros().add("Id não encontrado!");
			return ResponseEntity.badRequest().body(response);
		}

		this.lancamentoService.remover(id);
		return ResponseEntity.ok(new Response<String>());
	}
	
	@PutMapping("/id/{id}")
	public ResponseEntity<Response<LancamentoDto>> atualizarLancamento(@PathVariable Long id,@RequestBody LancamentoDto dto,BindingResult result){
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		
		this.validaFuncionario(dto, result);
		if(!this.lancamentoService.buscarPorLancamentoId(id).isPresent())
			result.addError(new ObjectError("lancamento", "Lancamento não encontrado (id) " + id));
		
		dto.setId(Optional.of(id));
		Lancamento lancamento = this.castDtoToEntity(dto, result);
		
		if(result.hasErrors()) {
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			response.setData(dto);
			return ResponseEntity.badRequest().body(response);
		}
		
		lancamento = this.lancamentoService.persistir(lancamento);
		response.setData(this.castEntityToDto(lancamento));
		return ResponseEntity.ok(response);
	}
	

	@PostMapping("/efetuarMarcacao/")
	public ResponseEntity<Response<LancamentoDto>> efetuarLancamento(@RequestBody @Valid LancamentoDto dto,BindingResult result) {
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		
		validaFuncionario(dto,result);
		Lancamento entity = this.castDtoToEntity(dto, result);
		
		if(result.hasErrors()) {
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			response.setData(dto);
			return ResponseEntity.badRequest().body(response);
		}
		
		Optional<Funcionario> funcionario = this.funcionarioService.getFuncionarioById(dto.getFuncionarioId());
		entity.setFuncionario(funcionario.get());
		this.lancamentoService.persistir(entity);
		response.setData(this.castEntityToDto(entity));
		return ResponseEntity.ok(response);
	}
	
	public Lancamento castDtoToEntity(LancamentoDto dto,BindingResult result) {
		Lancamento lancamento = new Lancamento();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(dto.getId().isPresent()) {
			Optional<Lancamento> lanc = this.lancamentoService.buscarPorLancamentoId(dto.getId().get());
			if(lanc.isPresent()) {
				lancamento = lanc.get();
			} else {
				result.addError(new ObjectError("Lancamento", "O lancamento não foi localizado pelo código fornecido."));
			}
		} else {
			
			lancamento.getFuncionario().setId(dto.getFuncionarioId());
		}
		
		
		lancamento.setLocalizacao(dto.getLocalizacao());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setFuncionario(new Funcionario());
		
		if(dto.getData() != null && !dto.getData().isEmpty()) {
			try {
				Date data = df.parse(dto.getData());
				lancamento.setData(data);
			} catch (ParseException e) {
				result.addError(new ObjectError("lancamento", "Data inválida!"));
			}
		}
		
		
		if(dto.getTipo() != null && !dto.getTipo().isEmpty()) {
			try {
				TipoEnum tipo = TipoEnum.valueOf(dto.getTipo());
				lancamento.setTipo(tipo);
			} catch(Exception ex) {
				result.addError(new ObjectError("lancamento","Tipo com formato incorreto!"));
			}
		}
		
		return lancamento;
	}
	
	public void validaFuncionario(LancamentoDto dto,BindingResult result) {
		if(dto.getFuncionarioId() != null) {
			if(!this.funcionarioService.getFuncionarioById(dto.getFuncionarioId()).isPresent()) {
				result.addError(new ObjectError("lancamento","Nenhum funcionário foi encontrado a partir do ID informado."));
			}
		} else {
			result.addError(new ObjectError("Lancamento", "O Id do funcionário é obrigatório!"));
		}
	}
	
	public LancamentoDto castEntityToDto(Lancamento lancamento) {
		LancamentoDto lancamentoDto = new LancamentoDto();
		lancamentoDto.setId(Optional.of(lancamento.getId()));
		lancamentoDto.setData(lancamento.getData().toString());
		lancamentoDto.setTipo(lancamento.getTipo().toString());
		lancamentoDto.setDescricao(lancamento.getDescricao());
		lancamentoDto.setLocalizacao(lancamento.getLocalizacao());
		lancamentoDto.setFuncionarioId(lancamento.getFuncionario().getId());
		return lancamentoDto;
	}
	
}
