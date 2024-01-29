package com.fejiro.exploration.dictionary.dictionary_web_api.config.database;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleGeneralAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleUserAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.UserAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.AuthorizedUserHolder;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.SimpleAuthorizedUserHolder;
import org.jooq.SQLDialect;
import org.jooq.conf.WriteIfReadonly;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.web.context.annotation.RequestScope;

import javax.sql.DataSource;
import java.util.Optional;

@Configuration
public class DatabaseConnectionConfiguration {
    @Autowired
    DataSource dataSource;

    @Bean
    public DataSourceConnectionProvider getDataSourceConnectionProvider() {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public DefaultDSLContext getDSLContext() {
        return new DefaultDSLContext(getJooqConfiguration());
    }

    @Bean
    public DefaultConfiguration getJooqConfiguration() {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(getDataSourceConnectionProvider());
        jooqConfiguration.set(SQLDialect.POSTGRES);
        jooqConfiguration.set(new DefaultExecuteListenerProvider(getJooqExceptionTranslator()));
        jooqConfiguration.settings().setReadonlyInsert(WriteIfReadonly.IGNORE);

        return jooqConfiguration;
    }

    @Bean
    JooqExceptionTranslator getJooqExceptionTranslator() {
        return new JooqExceptionTranslator();
    }
}
