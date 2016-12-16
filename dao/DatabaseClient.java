package com.rogers.ute.hhsessionsvc.dao;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseClient {

    Logger logger = LoggerFactory.getLogger(DatabaseClient.class);

    private Cluster cluster;
    private Session session;
    private String keyspace;
    private String password;
    private String userName;
    private String[] host;


    private DatabaseClient() {
        logger.info("DatabaseClient: in constructor");
    }

    public DatabaseClient(Config config) {
        logger.info("DatabaseClient: in constructor");
        host = config.getString("cassandra.host").split(";");
        userName = config.getString("hh-session-service.cassandra.user");
        password = config.getString("hh-session-service.cassandra.password");
        keyspace = config.getString("hh-session-service.cassandra.keyspace");
        cluster = Cluster.builder().addContactPoints(host)
                .withCredentials(userName, password)
                .build();
        session = cluster.connect(keyspace);
    }
    public ResultSet executeAsync(String query) throws Exception {
        ResultSet results = null;
        ResultSetFuture resultSetFuture = null;
        try {
            logger.info("Established new connection using: Host server - "
                    + host.toString() + "keyspace - " + keyspace);

            resultSetFuture = session.executeAsync(query);
            results = resultSetFuture.get();

            if (null != results) {
                logger.debug("Query executed: " + results.wasApplied()
                        + " query is-->" + query);
            }

        } catch (NoHostAvailableException exp) {
            logger.error("NoHostAvailableException occurred while executing the query --> " + query + " Exception: " + exp.getStackTrace());
            throw exp;
        } catch (QueryExecutionException exp) {
            logger.error("QueryExecutionException occurred while executing the query --> " + query + " Exception: " + exp.getStackTrace());
            throw exp;
        } catch (QueryValidationException exp) {
            logger.error("QueryValidationException occurred while executing the query --> " + query + " Exception: " + exp.getStackTrace());
            throw exp;
        }
        return results;
    }

}
