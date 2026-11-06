package kh.edu.istad.platform.customer.grpc.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import kh.edu.istad.platform.customer.grpc.CustomerGrpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GrpcServerConfig {
    @Bean
    public SmartLifecycle grpcServerLifecycle(CustomerGrpcService service,
                                              @Value("${grpc.server.port:9191}") int port) {
        Server server = ServerBuilder.forPort(port).addService(service).build();
        return new SmartLifecycle() {
            private volatile boolean running;
            @Override public void start() {
                try { server.start(); running = true; }
                catch (IOException e) { throw new IllegalStateException("gRPC start failed", e); }
            }
            @Override public void stop() { server.shutdown(); running = false; }
            @Override public boolean isRunning() { return running; }
        };
    }
}
