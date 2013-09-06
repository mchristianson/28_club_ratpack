package com.smdtest.twentyeightclub

import com.google.inject.AbstractModule
import groovy.sql.Sql

import java.sql.Connection
import java.sql.DriverManager

class DAOModule extends AbstractModule {

    private static ConfigObject config
    private static Sql _sql

    public DAOModule(File cfg) {
        config = new ConfigSlurper().parse(cfg.text).app
    }

    @Override
    protected void configure() {
        def sqlIn = getSql()
        bind(TwentyEightClubService).toInstance(new TwentyEightClubService(sqlIn))
    }

    private static Sql getSql() {
        if (_sql == null) {
            Sql.loadDriver("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://${config.mysql.host}/${config.mysql.db}", config.mysql.user, config.mysql.pass);
            _sql = new Sql(connection);
//            return newInstance(url, user, password);

//            _sql = groovy.sql.Sql.newInstance("jdbc:mysql://${config.mysql.host}/${config.mysql.db}", config.mysql.user, config.mysql.pass, "com.mysql.jdbc.Driver")
        }
        _sql
    }
}
