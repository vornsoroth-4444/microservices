package kh.edu.istad.platform.customer;

import kh.edu.istad.platform.customer.config.SecurityConfig;
import kh.edu.istad.platform.customer.domain.usecase.CreateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.InitiateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.InitiateDeactivateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.InitiateUpdateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.QueryCustomerUseCase;
import kh.edu.istad.platform.customer.restapi.controller.CustomerController;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerWebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityConfigTest {

    private MockMvc mockMvc;
    private QueryCustomerUseCase queryCustomerUseCase;

    @BeforeEach
    void setUp() {
        queryCustomerUseCase = mock(QueryCustomerUseCase.class);
        CustomerController controller = new CustomerController(
                mock(InitiateCustomerUseCase.class),
                mock(InitiateUpdateCustomerUseCase.class),
                mock(InitiateDeactivateCustomerUseCase.class),
                mock(CustomerWebMapper.class),
                mock(CreateCustomerUseCase.class),
                queryCustomerUseCase
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Verify CustomerController endpoint accepts requests")
    void testEndpointAccessible() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(queryCustomerUseCase.findById(any())).thenReturn(Mono.empty());

        mockMvc.perform(get("/api/customers/" + customerId))
                .andExpect(status().isOk());
    }
}
