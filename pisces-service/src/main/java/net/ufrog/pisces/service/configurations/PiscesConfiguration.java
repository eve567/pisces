package net.ufrog.pisces.service.configurations;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import net.ufrog.common.spring.SpringConfigurations;
import net.ufrog.common.spring.fastjson.FastJsonpHttpMessageConverter;
import net.ufrog.common.spring.interceptor.MultipartRequestInterceptor;
import net.ufrog.common.spring.interceptor.PropertiesInterceptor;
import net.ufrog.common.spring.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-18
 * @since 3.0.0
 */
@Configuration
public class PiscesConfiguration implements WebMvcConfigurer {

    /** 数据库属性管理器 */
    private final DatabasePropertiesManager databasePropertiesManager;

    @Value("${ufrog.app.messages}")
    private String basename;

    /**
     * 构造函数
     *
     * @param databasePropertiesManager 数据库属性管理器
     */
    @Autowired
    public PiscesConfiguration(DatabasePropertiesManager databasePropertiesManager) {
        this.databasePropertiesManager = databasePropertiesManager;
    }

    @Bean
    public PropertiesInterceptor propertiesInterceptor() {
        return SpringConfigurations.propertiesInterceptor(databasePropertiesManager);
    }

    @Bean
    public FastJsonpHttpMessageConverter jsonConverter() {
        FastJsonpHttpMessageConverter fastJsonpHttpMessageConverter = new FastJsonpHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        List<MediaType> lMediaType = new ArrayList<>();

        lMediaType.add(new MediaType("application", "json"));
        lMediaType.add(new MediaType("application", "javascript"));
        lMediaType.add(new MediaType("text", "javascript"));

        fastJsonConfig.setSerializerFeatures(SerializerFeature.BrowserCompatible, SerializerFeature.DisableCircularReferenceDetect);
        fastJsonpHttpMessageConverter.setSupportedMediaTypes(lMediaType);
        fastJsonpHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        fastJsonpHttpMessageConverter.setJsonp("jsonp");
        return fastJsonpHttpMessageConverter;
    }

    @Bean
    public MessageSource messageSource() {
        return SpringConfigurations.reloadableResourceBundleMessageSource(basename);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(propertiesInterceptor());
        registry.addInterceptor(new MultipartRequestInterceptor());
        registry.addInterceptor(new TokenInterceptor());
    }
}
