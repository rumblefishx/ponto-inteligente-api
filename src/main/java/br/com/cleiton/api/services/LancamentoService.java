package br.com.cleiton.api.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import br.com.cleiton.api.entities.Lancamento;

public interface LancamentoService {
	
	Optional<Lancamento> buscarPorLancamentoId(Long id);
	
	Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId,PageRequest page);
	
	Lancamento persistir(Lancamento lancamento);
	
	void remover(Long id);
}
