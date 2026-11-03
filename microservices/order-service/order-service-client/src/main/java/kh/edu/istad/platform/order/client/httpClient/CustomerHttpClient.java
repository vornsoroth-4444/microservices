package kh.edu.istad.platform.order.client.httpClient;

import kh.edu.istad.platform.order.domain.dto.CustomerResponse;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange("api/customers")
@ClientRegistrationId("customer-client")
public interface CustomerHttpClient {

    @GetExchange
    @ClientRegistrationId("customer-client")
    List<QueryCustomerResult> queryCustomer();

    @GetExchange("/{customerId}")
    @ClientRegistrationId("customer-client")
    CustomerResponse getCustomer(@PathVariable("customerId") String customerId);

    @GetExchange("/{id}")
    @ClientRegistrationId("customer-client")
    CustomerResponse getCustomerById(@PathVariable("id") String id);

    @PostExchange
    @ClientRegistrationId("customer-client")
    void createCustomer(@RequestBody QueryCustomerResult customer);
}
