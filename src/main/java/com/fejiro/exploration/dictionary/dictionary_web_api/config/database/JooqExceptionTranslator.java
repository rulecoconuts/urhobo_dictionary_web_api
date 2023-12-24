package com.fejiro.exploration.dictionary.dictionary_web_api.config.database;

import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.SQLDialect;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

public class JooqExceptionTranslator implements ExecuteListener {
    @Override
    public void exception(ExecuteContext ctx) {
        SQLDialect sqlDialect = ctx.configuration().dialect();
        SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(sqlDialect.name());
        ctx.exception(translator.translate("Access database using JOOQ", ctx.sql(), ctx.sqlException()));
    }
}
