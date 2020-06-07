package com.example.theatre;

import com.example.theatre.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Slf4j
@Configuration
public class Config {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    private static PaymentService getPaymentService(){
        log.info("creating mock");
        return Mockito.mock(PaymentService.class);
    }
}
