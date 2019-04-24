package br.com.cleiton.api.services;

import java.util.Optional;

import br.com.cleiton.api.entities.Empresa;

public interface EmpresaService {
	
	/**
	 * Método capaz de localizar uma empresa por meio do seu respectivo CNPJ.
	 * @param cnpj - valor do CNPJ que será utilizado para busca.
	 * @return retorna a empresa encontrada.
	 */
	Optional<Empresa> findCompanyByCnpj(String cnpj);
	
	/**
	 * Método capaz de persistir a empresa que se deseja cadastrar.
	 * @param empresa - objeto do tipo empresa que será persistido em banco.
	 * @return - retorna a entidade persistida.
	 */
	Optional<Empresa> saveCompany(Empresa empresa);
}
