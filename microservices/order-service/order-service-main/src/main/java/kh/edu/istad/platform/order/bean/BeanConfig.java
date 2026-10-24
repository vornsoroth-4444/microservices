package kh.edu.istad.platform.order.bean;

import kh.edu.istad.platform.order.domain.service.OrderDomainService;
import kh.edu.istad.platform.order.domain.service.OrderDomainServiceImpl;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

    /**
     * Standard RestTemplate for Part 1 (Hard-Coded Service Addresses)
     */
    @Bean(name = "directRestTemplate")
    public RestTemplate directRestTemplate() {
        return new RestTemplate();
    }

    /**
     * @LoadBalanced RestTemplate for Part 3 & 4 (Eureka Service Discovery & Load Balancing)
     */
    @Bean(name = "loadBalancedRestTemplate")
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        return new RestTemplate();
    }
}
