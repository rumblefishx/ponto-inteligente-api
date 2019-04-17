package br.com.cleiton.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cleiton.api.entities.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long>{

	Funcionario findByCpf(String cpf);
	
	Funcionario findByEmail(String email);
	
	Funcionario findByCpfOrEmail(String cpf, String email);
}
