package br.com.cleiton.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cleiton.api.entities.Empresa;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class EmpresaRepositoryTest {
	
	@Autowired
	private EmpresaRepository repository;
	
	private static final String DEFAULT_RAZAO_SOCIAL = "HAVAIANAS SA.";
	private static final String DEFAULT_CNPJ = "407.862.338-77";
	private static final Long DEFAULT_ID = 1L;
	private static final Date DEFAULT_DATE = new Date();
	
	private static final String FAIL_CNPJ = "1234567";

	@Test
	public void findByCnpjOkTest(){
		Empresa empresa = repository.findByCnpj(DEFAULT_CNPJ);
		assertNotNull(empresa);
		assertEquals(empresa.getCnpj(),DEFAULT_CNPJ);
	}
	
	@Test
	public void findByCnpjNotOkTest(){
		Empresa empresa = repository.findByCnpj(FAIL_CNPJ);
		assertNull(empresa);
	}
	
	
	@Before
	public void setUpTest() {
		Empresa empresa = new Empresa();
		empresa.setId(DEFAULT_ID);
		empresa.setRazaoSocial(DEFAULT_RAZAO_SOCIAL);
		empresa.setCnpj(DEFAULT_CNPJ);
		empresa.setDataCriacao(DEFAULT_DATE);
		empresa.setDataAtualizacao(DEFAULT_DATE);
		
		repository.save(empresa);
	}
	
	@After
	public void tearDown() {
		repository.deleteAll();
	}
}
