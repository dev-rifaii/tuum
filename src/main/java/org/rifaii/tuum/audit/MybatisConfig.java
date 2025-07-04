package org.rifaii.tuum.audit;

import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {

    @Bean
    public Interceptor dataChangeInterceptor(DataChangeNotifier dataChangeNotifier) {
        return new DataChangeStatementInterceptor(dataChangeNotifier);
    }

}
