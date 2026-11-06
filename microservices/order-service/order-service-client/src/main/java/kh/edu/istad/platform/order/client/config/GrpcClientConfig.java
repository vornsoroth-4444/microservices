package kh.edu.istad.platform.order.client.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import build.buf.gen.istad.customer.v1.CustomerServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
    @Bean(destroyMethod = "shutdown")
    public ManagedChannel customerChannel(@Value("${grpc.client.customer-service.host:localhost}") String host,
                                          @Value("${grpc.client.customer-service.port:9191}") int port) {
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    }

    @Bean
    public CustomerServiceGrpc.CustomerServiceStub customerServiceStub(ManagedChannel ch) {
        return CustomerServiceGrpc.newStub(ch);
    }
}
