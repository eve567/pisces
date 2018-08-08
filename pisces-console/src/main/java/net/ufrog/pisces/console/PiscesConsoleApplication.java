package net.ufrog.pisces.console;

import net.ufrog.common.spring.SpringConfigurations;
import net.ufrog.common.spring.exception.*;
import net.ufrog.leo.client.app.LeoUserIdConverter;
import net.ufrog.leo.client.configuration.LeoInterception;
import net.ufrog.pisces.domain.models.App;
import net.ufrog.pisces.domain.repositories.AppRepository;
import net.ufrog.pisces.service.AppService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-26
 * @since 3.0.0
 */
@EntityScan(basePackageClasses = App.class)
@EnableJpaRepositories(basePackageClasses = AppRepository.class)
@SpringBootApplication(scanBasePackageClasses = {PiscesConsoleApplication.class, AppService.class})
@EnableDiscoveryClient
@EnableHystrix
public class PiscesConsoleApplication implements WebMvcConfigurer {

    @Value("${ufrog.leo.host}")
    private String leoHost;

    /**
     * @param args arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PiscesConsoleApplication.class, args);
    }

    @Bean
    public LeoUserIdConverter leoUserIdConverter() {
        return new LeoUserIdConverter() {

            @Override
            public String toAppUserId(String leoAppUserId) {
                return leoAppUserId;
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LeoInterception());
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        List<ExceptionHandler> lExceptionHandler = new ArrayList<>(3);
        lExceptionHandler.add(new ServiceExceptionHandler());
        lExceptionHandler.add(new InvalidArgumentExceptionHandler());
        lExceptionHandler.add(new NotSignExceptionHandler("sign", leoHost + "/sign_out", "_not_sign::"));
        setExceptionHandler(lExceptionHandler, exceptionResolvers);
    }

    /**
     * @param lExceptionHandler 异常列表
     * @param lHandlerExceptionResolver 异常处理列表
     */
    public static void setExceptionHandler(List<ExceptionHandler> lExceptionHandler, List<HandlerExceptionResolver> lHandlerExceptionResolver) {
        Optional<HandlerExceptionResolver> oHandlerExceptionResolver = lHandlerExceptionResolver.stream().filter(handlerExceptionResolver -> handlerExceptionResolver.getClass() == ExceptionResolver.class).findFirst();
        ExceptionResolver exceptionResolver;
        if (oHandlerExceptionResolver.isPresent()) {
            exceptionResolver = (ExceptionResolver) oHandlerExceptionResolver.get();
        } else {
            exceptionResolver = SpringConfigurations.exceptionResolver(null, null, "result", "", null, null);
            lHandlerExceptionResolver.add(exceptionResolver);
        }
        lExceptionHandler.forEach(exceptionHandler -> exceptionResolver.getExceptionHandlers().add(exceptionHandler));
    }
}
