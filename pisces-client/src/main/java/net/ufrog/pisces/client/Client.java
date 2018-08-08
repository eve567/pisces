package net.ufrog.pisces.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-16
 * @since 3.0.0
 */
@EnableFeignClients(basePackageClasses = JobClient.class)
@ComponentScan(basePackageClasses = JobClientFallbackFactory.class)
@ConditionalOnMissingClass("net.ufrog.pisces.server.controllers.JobController")
public class Client {

    /** 构造函数 */
    private Client() {}

    /** client name */
    static final String NAME = "pisces-server-3";
}
