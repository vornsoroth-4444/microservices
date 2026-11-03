package kh.edu.istad.common.webflux.cofig;

import kh.edu.istad.common.webflux.exeption.DomainExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class WebConfig {
 @Bean
    public DomainExceptionHandler domainExceptionHandler(){
     return new DomainExceptionHandler();
 }
}
