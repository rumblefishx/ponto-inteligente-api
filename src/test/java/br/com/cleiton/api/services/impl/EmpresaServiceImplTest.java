package br.com.cleiton.api.services.impl;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cleiton.api.entities.Empresa;
import br.com.cleiton.api.repositories.EmpresaRepository;
import br.com.cleiton.api.services.EmpresaService;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value="test")
public class EmpresaServiceImplTest {
	
	private static final String CNPJ = "547862338775";
	private static final String RAZAO_SOCIAL = "Matrix";
	
	@Autowired
	EmpresaService service;
	
	@MockBean
	EmpresaRepository repository;
	
	@Before
	public void setUp() {
		BDDMockito.given(this.repository.findByCnpj(Mockito.anyString())).willReturn(new Empresa());
		BDDMockito.given(this.repository.findByRazaoSocial(Mockito.anyString())).willReturn(new Empresa());
		BDDMockito.given(this.repository.save(Mockito.any(Empresa.class))).willReturn(new Empresa());
	}
	
	@Test
	public void findCompanyByCnpjTest(){
		Optional<Empresa> empresa = this.service.findCompanyByCnpj(CNPJ);
		assertTrue(empresa.isPresent());
	}
	
	@Test
	public void findCompanyByRazaoSocialTest(){
		Optional<Empresa> empresa = this.service.findCompanyByRazaoSocial(RAZAO_SOCIAL);
		assertTrue(empresa.isPresent());
	}
	
	@Test
	public void persistEmpresaTest() {
		Optional<Empresa> empresa = this.service.saveCompany(new Empresa());
		assertTrue(empresa.isPresent());
	}

}
