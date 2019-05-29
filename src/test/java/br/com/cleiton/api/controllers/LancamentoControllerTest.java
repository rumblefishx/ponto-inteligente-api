package br.com.cleiton.api.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cleiton.api.entities.Empresa;
import br.com.cleiton.api.entities.Funcionario;
import br.com.cleiton.api.entities.Lancamento;
import br.com.cleiton.api.enums.PerfilEnum;
import br.com.cleiton.api.enums.TipoEnum;
import br.com.cleiton.api.services.EmpresaService;
import br.com.cleiton.api.services.LancamentoService;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class LancamentoControllerTest {

	@Autowired
	private MockMvc mock;
	
	@MockBean
	private LancamentoService lancamentoService;
	
	private static final String DEFAULT_DESCRIPTION = "Inicio de jornada.";
	
	private static final Long DEFAULT_ID = 1L;
	
	private static final String DEFAULT_LOCATION = "São Paulo";
	
	private static final String DEFAULT_CPF = "383.448.700-70";

	private static final String DEFAULT_EMAIL = "funcionario@empresa.com";

	private static final String DEFAULT_FUNC_NAME = "fulano beltrano";

	private static final Float DEFAULT_FLOAT = 100F;

	private static final BigDecimal DEFAULT_BIG_DECIMAL = BigDecimal.TEN;

	private static final String DEFAULT_PASSWORD = "123456";
	
	private static final String DEFAULT_URI = "/api/lancamento/id/1";
	
	
	

	
	private final static String URI = "/api/empresa/cnpj/";
	private final static String DEFAULT_CNPJ = "82198127000121";
	
	private final static String DEFAULT_RAZAO_SOCIAL = "Kazale IT";
	
	@MockBean
	private EmpresaService empresaService;
	
	
	@Ignore
	@Test
	public void getCompanyByCnpjSucess() throws Exception {
		BDDMockito.given(this.empresaService.findCompanyByCnpj(Mockito.anyString())).willReturn(this.getDefaultCompany());
		
		mock.perform(MockMvcRequestBuilders.get(URI + DEFAULT_CNPJ).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(DEFAULT_ID))
		.andExpect(jsonPath("$.cnpj").value(DEFAULT_CNPJ))
		.andExpect(jsonPath("$.razaoSocial").value(DEFAULT_RAZAO_SOCIAL));
	}
	
	

	@WithMockUser
	@Test
	public void buscarLancamentoPorIdTest() throws Exception {
		BDDMockito.given(this.lancamentoService.buscarPorLancamentoId(Mockito.anyLong()))
		.willReturn(mockLancamento());
		
		mock.perform(MockMvcRequestBuilders.post(DEFAULT_URI)
				.content(this.obterJson())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	private Optional<Lancamento> mockLancamento() {
		Lancamento lanc = new Lancamento();
		
		Date baseDate = new Date();
		
		lanc.setData(baseDate);
		lanc.setDataAtualizacao(baseDate);
		lanc.setDataCriacao(baseDate);
		lanc.setDescricao(DEFAULT_DESCRIPTION);
		lanc.setId(DEFAULT_ID);
		lanc.setLocalizacao(DEFAULT_LOCATION);
		lanc.setTipo(TipoEnum.TERMINO_TRABALHO);
		//lanc.setFuncionario(mockFuncionario());
		
		return Optional.ofNullable(lanc);
	}
	
	private Funcionario mockFuncionario() {
		Funcionario func = new Funcionario();
		
		Date baseDate = new Date();
		
		func.setCpf(DEFAULT_CPF);
		func.setDataAtualizacao(baseDate);
		func.setDataCriacao(baseDate);
		func.setEmail(DEFAULT_EMAIL);
		func.setEmpresa(new Empresa());
		func.setId(DEFAULT_ID);
		func.setNome(DEFAULT_FUNC_NAME);
		func.setPerfil(PerfilEnum.ROLE_ADMIN);
		func.setQtdHorasAlmoco(DEFAULT_FLOAT);
		func.setQtdHorasTrabalhoDia(DEFAULT_FLOAT);
		func.setValorHora(DEFAULT_BIG_DECIMAL);
		func.setSenha(DEFAULT_PASSWORD);
		
		return func;
	}
	
	private Optional<Empresa> getDefaultCompany() {
		Empresa company = new Empresa();
		company.setId(DEFAULT_ID);
		company.setRazaoSocial(DEFAULT_RAZAO_SOCIAL);
		company.setCnpj(DEFAULT_CNPJ);
		return Optional.ofNullable(company);
	}
	
	
	public String obterJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(mockLancamento().get());
	}
}
