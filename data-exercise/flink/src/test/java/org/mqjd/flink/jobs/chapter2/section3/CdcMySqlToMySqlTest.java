package org.mqjd.flink.jobs.chapter2.section3;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.flink.core.execution.JobClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mqjd.flink.containers.ContainerBaseTest;
import org.mqjd.flink.containers.ContainerType;
import org.mqjd.flink.containers.mysql.UniqueDatabase;
import org.mqjd.flink.jobs.CommandArgs;
import org.mqjd.flink.util.JdbcUtil;
import org.mqjd.flink.util.TimerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdcMySqlToMySqlTest extends ContainerBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(CdcMySqlToMySqlTest.class);
    private static final String CHAPTER = "chapter2";
    private static final String SECTION = "section3";

    private final UniqueDatabase sourceDatabase = new UniqueDatabase(getContainer(ContainerType.MYSQL),
        "chapter2_section3_source", "hd_user", "hd_user_password");

    private final UniqueDatabase targetDataBase = new UniqueDatabase(getContainer(ContainerType.MYSQL),
        "chapter2_section3_target", "hd_user", "hd_user_password");

    @BeforeClass
    public static void startContainers() {
        ContainerBaseTest.startContainers(ContainerType.MYSQL);
    }

    @Test
    public void given_correct_input_and_output_when_execute_then_success() throws Exception {
        sourceDatabase.createAndInitialize(CHAPTER, SECTION);
        targetDataBase.createAndInitialize(CHAPTER, SECTION);
        CompletableFuture<JobClient> jobClientFuture = executeJobAsync(() -> {
            try {
                String[] params = CommandArgs.builder()
                    .defaultKey("D")
                    .kvOption("source.port", sourceDatabase.getDatabasePort())
                    .kvOption("source.hostname", sourceDatabase.getHost())
                    .kvOption("source.username", sourceDatabase.getUsername())
                    .kvOption("source.password", sourceDatabase.getPassword())
                    .kvOption("source.database-name", sourceDatabase.getDatabaseName())
                    .kvOption("sink.url", targetDataBase.getJdbcUrl())
                    .kvOption("sink.username", targetDataBase.getUsername())
                    .kvOption("sink.password", targetDataBase.getPassword())
                    .kvOption("sink.database-name", targetDataBase.getDatabaseName())
                    .build();
                CdcMySqlToMySql.main(params);
            } catch (Exception e) {
                LOG.error("Error execute CdcMySqlToMySql", e);
            }
        });
        CompletableFuture<Boolean> sinkSuccess = new CompletableFuture<>();
        ScheduledFuture<?> interval = TimerUtil.interval(() -> {
            List<Map<String, Object>> result =
                JdbcUtil.query(targetDataBase.getJdbcConnection(), "SELECT * FROM user_target");
            if (result.size() == 20) {
                sinkSuccess.complete(true);
            }
        }, 1000);

        Boolean success = sinkSuccess.get(3, TimeUnit.MINUTES);
        assertTrue(success);
        interval.cancel(true);
        jobClientFuture.get().cancel().get(10, TimeUnit.SECONDS);
    }
}