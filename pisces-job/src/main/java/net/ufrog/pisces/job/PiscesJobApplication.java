package net.ufrog.pisces.job;

import net.ufrog.pisces.domain.models.App;
import net.ufrog.pisces.domain.repositories.AppRepository;
import net.ufrog.pisces.service.AppService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-18
 * @since 3.0.0
 */
@EntityScan(basePackageClasses = App.class)
@EnableJpaRepositories(basePackageClasses = AppRepository.class)
@SpringBootApplication(scanBasePackageClasses = {PiscesJobApplication.class, AppService.class})
@EnableDiscoveryClient
@EnableHystrix
public class PiscesJobApplication {

    /**
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(PiscesJobApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
