package br.com.cleiton.api.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import br.com.cleiton.api.entities.Lancamento;
import br.com.cleiton.api.repositories.LancamentoRepository;
import br.com.cleiton.api.services.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {
	
	@Autowired
	LancamentoRepository repository;

	@Override
	@Cacheable("lancamentosPorId")
	public Optional<Lancamento> buscarPorLancamentoId(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest page) {
		return repository.findByFuncionarioId(funcionarioId, page);
	}

	@Override
	@CachePut("lancamentosPorId")
	public Lancamento persistir(Lancamento lancamento) {
		return repository.save(lancamento);
	}

	@Override
	public void remover(Long id) {
		this.repository.deleteById(id);
	}

}
