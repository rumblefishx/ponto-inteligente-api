package br.com.cleiton.api.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import br.com.cleiton.api.entities.Empresa;
import br.com.cleiton.api.services.EmpresaService;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
/**
 * Classe tal
 * @author cleiton
 *
 */
public class EmpresaControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private EmpresaService empresaService;
	
	private final static String DEFAULT_CNPJ = "82198127000121";
	
	private final static String DEFAULT_RAZAO_SOCIAL = "Kazale IT";
	
	private final static Long DEFAULT_ID = Long.getLong("1");
	
	private final static String URI = "/api/empresa/cnpj/";
	
	@Ignore
	@Test
	public void getCompanyByCnpjSucess() throws Exception {
		BDDMockito.given(this.empresaService.findCompanyByCnpj(Mockito.anyString())).willReturn(this.getDefaultCompany());
		
		mockMvc.perform(MockMvcRequestBuilders.get(URI + DEFAULT_CNPJ).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(DEFAULT_ID))
		.andExpect(jsonPath("$.cnpj").value(DEFAULT_CNPJ))
		.andExpect(jsonPath("$.razaoSocial").value(DEFAULT_RAZAO_SOCIAL));
	}
	
	@Ignore
	@Test
	public void getCompanyByCnpjError() throws Exception {
		BDDMockito.given(this.empresaService.findCompanyByCnpj(Mockito.anyString())).willReturn(Optional.empty());
		
		mockMvc.perform(MockMvcRequestBuilders.get(URI + DEFAULT_CNPJ).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.erros").value("Nenhuma empresa foi localizada a partir do CNPJ " + DEFAULT_CNPJ));
	}

	private Optional<Empresa> getDefaultCompany() {
		Empresa company = new Empresa();
		company.setId(DEFAULT_ID);
		company.setRazaoSocial(DEFAULT_RAZAO_SOCIAL);
		company.setCnpj(DEFAULT_CNPJ);
		return Optional.ofNullable(company);
	}
}
