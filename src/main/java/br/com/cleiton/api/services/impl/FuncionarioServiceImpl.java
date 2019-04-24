package br.com.cleiton.api.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cleiton.api.entities.Funcionario;
import br.com.cleiton.api.repositories.FuncionarioRepository;
import br.com.cleiton.api.services.FuncionarioService;

@Service
public class FuncionarioServiceImpl implements FuncionarioService {

	@Autowired
	private FuncionarioRepository repository;
	
	@Override
	public Optional<Funcionario> getFuncionarioByCpf(String cpf) {
		return Optional.ofNullable(repository.findByCpf(cpf));
	}

	@Override
	public Optional<Funcionario> getFuncionarioByEmail(String email) {
		return Optional.ofNullable(repository.findByEmail(email));
	}

	@Override
	public Optional<Funcionario> getFuncionarioByEmailOrCpf(String cpf, String email) {
		return Optional.ofNullable(repository.findByCpfOrEmail(cpf, email));
	}

	@Override
	public Funcionario saveFuncionario(Funcionario entity) {
		return repository.save(entity);
	}

	@Override
	public Optional<Funcionario> getFuncionarioById(Long id) {
		return repository.findById(id);
	}

	
}
