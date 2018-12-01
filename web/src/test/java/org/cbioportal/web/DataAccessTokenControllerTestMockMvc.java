package org.cbioportal.web;

import org.cbioportal.web.config.DataAccessTokenControllerConfig;
import org.cbioportal.model.DataAccessToken;
import org.cbioportal.service.DataAccessTokenService;
import org.cbioportal.service.exception.DataAccessTokenNoUserIdentityException;
import org.cbioportal.service.exception.DataAccessTokenProhibitedUserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cbioportal.model.ClinicalAttribute;
import org.cbioportal.model.ClinicalAttributeCount;
import org.cbioportal.model.meta.BaseMeta;
import org.cbioportal.service.ClinicalAttributeService;
import org.cbioportal.service.exception.ClinicalAttributeNotFoundException;
import org.cbioportal.web.parameter.HeaderKeyConstants;
import org.cbioportal.web.parameter.ClinicalAttributeCountFilter;
import org.cbioportal.web.parameter.SampleIdentifier;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.TestPropertySource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.cbioportal.web.config.TokenAuthenticationFilter;
import org.springframework.context.annotation.ImportResource;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import javax.servlet.http.HttpSession;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext-web.xml", "/applicationContext-security.xml"})
@WebAppConfiguration
@Configuration
public class DataAccessTokenControllerTestMockMvc {
    public static final String MOCK_TOKEN_STRING = "MOCK_TOKEN_STRING"; 
    public static final DataAccessToken MOCK_TOKEN_INFO = new DataAccessToken(MOCK_TOKEN_STRING);

    @Bean
    public DataAccessTokenService tokenService() {
        return Mockito.mock(DataAccessTokenService.class);
    }

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private DataAccessTokenService tokenService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private FilterChainProxy filterChainProxy;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(filterChainProxy).build();
    }

    private HttpSession getSession(String user, String password) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/j_spring_security_check")
                               .param("j_username", user)
                               .param("j_password", password))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getRequest()
            .getSession();
    }
    @Test
    public void createTokenValidUserTest() throws Exception {
        // user and password should match what is in test version of security context xml
        HttpSession session = getSession("cbioportal-user", "password");
        Mockito.when(tokenService.createDataAccessToken(Matchers.any(), Matchers.anyBoolean())).thenReturn(MOCK_TOKEN_INFO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/data-access-tokens")
                                           .session((MockHttpSession)session)
                                           .accept(MediaType.APPLICATION_JSON)
                                           .contentType(MediaType.APPLICATION_JSON)
                                           .param("allowRevocationOfTokens", "true"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
        System.out.println("Response: " + result.getResponse().getContentAsString() + "   " + result.getHandler() + "   " + result.getResponse().getStatus());
    }

}
