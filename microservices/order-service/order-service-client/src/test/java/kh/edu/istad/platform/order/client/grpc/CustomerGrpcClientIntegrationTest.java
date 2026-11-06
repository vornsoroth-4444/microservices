package kh.edu.istad.platform.order.client.grpc;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import kh.edu.istad.platform.order.domain.dto.GetCustomerResult;
import build.buf.gen.istad.customer.v1.Customer;
import build.buf.gen.istad.customer.v1.CustomerServiceGrpc;
import build.buf.gen.istad.customer.v1.CustomerStatus;
import build.buf.gen.istad.customer.v1.GetCustomerRequest;
import build.buf.gen.istad.customer.v1.GetCustomerResponse;
import build.buf.gen.istad.customer.v1.ListCustomersRequest;
import build.buf.gen.istad.customer.v1.ListCustomersResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerGrpcClientIntegrationTest {

    private Server inProcessServer;
    private ManagedChannel channel;
    private CustomerGrpcClient grpcClient;

    @BeforeEach
    void setUp() throws IOException {
        String serverName = InProcessServerBuilder.generateName();

        CustomerServiceGrpc.CustomerServiceImplBase serviceImpl = new CustomerServiceGrpc.CustomerServiceImplBase() {
            @Override
            public void getCustomer(GetCustomerRequest request, StreamObserver<GetCustomerResponse> responseObserver) {
                Customer customer = Customer.newBuilder()
                        .setCustomerId(request.getCustomerId())
                        .setUsername("johndoe")
                        .setFamilyName("Doe")
                        .setGivenName("John")
                        .setStatus(CustomerStatus.CUSTOMER_STATUS_ACTIVE)
                        .build();
                responseObserver.onNext(GetCustomerResponse.newBuilder().setCustomer(customer).build());
                responseObserver.onCompleted();
            }

            @Override
            public void listCustomers(ListCustomersRequest request, StreamObserver<ListCustomersResponse> responseObserver) {
                Customer c1 = Customer.newBuilder()
                        .setCustomerId("1")
                        .setUsername("user1")
                        .setFamilyName("A")
                        .setGivenName("One")
                        .setStatus(CustomerStatus.CUSTOMER_STATUS_ACTIVE)
                        .build();
                Customer c2 = Customer.newBuilder()
                        .setCustomerId("2")
                        .setUsername("user2")
                        .setFamilyName("B")
                        .setGivenName("Two")
                        .setStatus(CustomerStatus.CUSTOMER_STATUS_INACTIVE)
                        .build();

                responseObserver.onNext(ListCustomersResponse.newBuilder().setCustomer(c1).build());
                responseObserver.onNext(ListCustomersResponse.newBuilder().setCustomer(c2).build());
                responseObserver.onCompleted();
            }
        };

        inProcessServer = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(serviceImpl)
                .build()
                .start();

        channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();

        CustomerServiceGrpc.CustomerServiceStub stub = CustomerServiceGrpc.newStub(channel);
        grpcClient = new CustomerGrpcClient(stub);
    }

    @AfterEach
    void tearDown() {
        if (channel != null) {
            channel.shutdownNow();
        }
        if (inProcessServer != null) {
            inProcessServer.shutdownNow();
        }
    }

    @Test
    void getCustomer_returnsCustomerResult() {
        StepVerifier.create(grpcClient.getCustomer("cust-123"))
                .assertNext(result -> {
                    assertEquals("cust-123", result.customerId());
                    assertEquals("johndoe", result.username());
                    assertEquals("Doe", result.familyName());
                    assertEquals("John", result.givenName());
                    assertEquals("CUSTOMER_STATUS_ACTIVE", result.status());
                })
                .verifyComplete();
    }

    @Test
    void listCustomers_returnsFluxOfCustomers() {
        StepVerifier.create(grpcClient.listCustomers())
                .assertNext(result -> {
                    assertEquals("1", result.customerId());
                    assertEquals("user1", result.username());
                })
                .assertNext(result -> {
                    assertEquals("2", result.customerId());
                    assertEquals("user2", result.username());
                })
                .verifyComplete();
    }
}
