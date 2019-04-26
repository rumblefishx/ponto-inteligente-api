package br.com.cleiton.api.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cleiton.api.entities.Funcionario;
import br.com.cleiton.api.entities.Lancamento;
import br.com.cleiton.api.enums.TipoEnum;
import br.com.cleiton.api.repositories.LancamentoRepository;
import br.com.cleiton.api.services.LancamentoService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoServiceImplTest {
	
	private static final Long ID_PADRAO_LANCAMENTO = 1L;
	
	private static final Long ID_PADRAO_FUNCIONARIO = 2L;

	private static final String DESCRICAO_PADRAO = "DESCRICAO!";

	@Autowired
	LancamentoService service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Before
	public void setUp() {
		BDDMockito.given(repository.findById(Mockito.anyLong())).willReturn(Optional.of(new Lancamento()));	
		BDDMockito.given(repository.findByFuncionarioId(Mockito.anyLong(), Mockito.any(PageRequest.class))).willReturn(new PageImpl<Lancamento>(new ArrayList<Lancamento>()));
		BDDMockito.given(repository.save(Mockito.any(Lancamento.class))).willReturn(new Lancamento());
	}
	
	@Test
	public void lancamentoPorIdTest() {
		Optional<Lancamento> lancamento = this.service.buscarPorLancamentoId(ID_PADRAO_LANCAMENTO);
		assertTrue(lancamento.isPresent());
	}
	
	@Test
	public void lancamentoPorFuncionarioIdTest() {
		Page<Lancamento> lancamento = this.service.buscarPorFuncionarioId(ID_PADRAO_FUNCIONARIO, new PageRequest(0, 10));
		assertNotNull(lancamento);
	}
	
	@Test
	public void salvarLancamentoTest() {
		Lancamento lancamento = this.service.persistir(getLancamentoPadrao());
		assertNotNull(lancamento);
	}

	private Lancamento getLancamentoPadrao() {
		Lancamento lancamento = new Lancamento();
		lancamento.setData(new Date());
		lancamento.setDataAtualizacao(new Date());
		lancamento.setDataCriacao(new Date());
		lancamento.setDescricao(DESCRICAO_PADRAO);
		lancamento.setFuncionario(new Funcionario());
		lancamento.setId(ID_PADRAO_LANCAMENTO);
		lancamento.setLocalizacao(null);
		lancamento.setTipo(TipoEnum.INICIO_TRABALHO);
		return lancamento;
	}
}
