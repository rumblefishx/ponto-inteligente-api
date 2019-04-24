package br.com.cleiton.api.services;

import java.util.Optional;

import br.com.cleiton.api.entities.Funcionario;

public interface FuncionarioService {
	
	public Optional<Funcionario> getFuncionarioById(Long id);

	public Optional<Funcionario> getFuncionarioByCpf(String cpf);
	
	public Optional<Funcionario> getFuncionarioByEmail(String email);
	
	public Optional<Funcionario> getFuncionarioByEmailOrCpf(String cpf,String email);
	
	public Funcionario saveFuncionario(Funcionario entity); 	
}
