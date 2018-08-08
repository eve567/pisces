package net.ufrog.pisces.server;

import net.ufrog.common.spring.springboot.SpringWebAppConfiguration;
import net.ufrog.pisces.domain.models.App;
import net.ufrog.pisces.domain.repositories.AppRepository;
import net.ufrog.pisces.server.controllers.JobController;
import net.ufrog.pisces.service.AppService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-16
 * @since 3.0.0
 */
@EntityScan(basePackageClasses = App.class)
@EnableJpaRepositories(basePackageClasses = AppRepository.class)
@SpringBootApplication(scanBasePackageClasses = {PiscesServerApplication.class, AppService.class})
@EnableDiscoveryClient
@EnableHystrix
@EnableSwagger2
@EnableConfigurationProperties(SpringWebAppConfiguration.AppProperties.class)
public class PiscesServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PiscesServerApplication.class, args);
    }

    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage(JobController.class.getPackage().getName())).build();
    }

    @Bean
    public FilterRegistrationBean<PiscesAppFilter> piscesAppFilter(SpringWebAppConfiguration.AppProperties appProperties, PiscesJobManager piscesJobManager) {
        FilterRegistrationBean<PiscesAppFilter> filterRegistrationBean = new FilterRegistrationBean<>();

        net.ufrog.common.app.App.setConfigHelper(appProperties.get());
        filterRegistrationBean.setFilter(new PiscesAppFilter(piscesJobManager));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("piscesAppFilter");
        return filterRegistrationBean;
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
