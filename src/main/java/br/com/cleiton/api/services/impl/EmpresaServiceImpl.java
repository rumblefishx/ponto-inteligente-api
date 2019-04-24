package br.com.cleiton.api.services.impl;

import java.util.Optional;
import org.jboss.logging.Logger;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cleiton.api.entities.Empresa;
import br.com.cleiton.api.repositories.EmpresaRepository;
import br.com.cleiton.api.services.EmpresaService;

@Service
public class EmpresaServiceImpl implements EmpresaService {

	@Autowired
	private EmpresaRepository repository;
	
	private Logger log = LoggerFactory.logger(EmpresaRepository.class);
	
	@Override
	public Optional<Empresa> findCompanyByCnpj(String cnpj) {
		log.info("Buscando empresa por meio do CNPJ " + cnpj);
		return Optional.ofNullable(repository.findByCnpj(cnpj));
	}

	@Override
	public Optional<Empresa> saveCompany(Empresa empresa) {
		log.info("Persistindo empresa");
		return Optional.ofNullable(repository.save(empresa));
	}

}
