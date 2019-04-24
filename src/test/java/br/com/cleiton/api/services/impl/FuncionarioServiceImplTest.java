package br.com.cleiton.api.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cleiton.api.entities.Funcionario;
import br.com.cleiton.api.repositories.FuncionarioRepository;
import br.com.cleiton.api.services.FuncionarioService;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class FuncionarioServiceImplTest {
	
	private final static String EMAIL = "cleiton.antunes.kkk@gmail.com";
	
	private static final String CPF = "408.632.444-93";
	
	private static final Long ID = 1L;

	@Autowired
	private FuncionarioService service;
	
	@MockBean
	private FuncionarioRepository repository;
	
	@Before
	public void setUp() {
		BDDMockito.given(this.repository.save(Mockito.any(Funcionario.class))).willReturn(new Funcionario());
		BDDMockito.given(this.repository.findByCpf(Mockito.anyString())).willReturn(new Funcionario());
		BDDMockito.given(this.repository.findByEmail(Mockito.anyString())).willReturn(new Funcionario());
		BDDMockito.given(this.repository.findByCpfOrEmail(Mockito.anyString(), Mockito.anyString())).willReturn(new Funcionario());
		BDDMockito.given(this.repository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(new Funcionario()));
	}
		
	@Test
	public void findFuncionarioByCpfTest() {
		Optional<Funcionario> funcionario = this.service.getFuncionarioByCpf(CPF);
		assertTrue(funcionario.isPresent());
	}
	
	@Test
	public void findFuncionarioByEmailTest() {
		Optional<Funcionario> funcionario = this.service.getFuncionarioByEmail(EMAIL);
		assertTrue(funcionario.isPresent());
	}
	
	@Test
	public void findFuncionarioByEmailOrCpfTest() {
		Optional<Funcionario> funcionario = this.service.getFuncionarioByEmailOrCpf(CPF, EMAIL);
		assertTrue(funcionario.isPresent());
	}
	
	@Test
	public void findFuncionarioByIdTest() {
		Optional<Funcionario> funcionario = this.service.getFuncionarioById(ID);
		assertTrue(funcionario.isPresent());
	}
	
	@Test
	public void persistFuncionarioTest() {
		Funcionario funcionario = this.service.saveFuncionario(new Funcionario());
		assertNotNull(funcionario);
	}
}
