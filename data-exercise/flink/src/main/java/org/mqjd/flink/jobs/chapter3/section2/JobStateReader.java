package org.mqjd.flink.jobs.chapter3.section2;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.collections.IteratorUtils;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.client.cli.CliFrontend;
import org.apache.flink.client.cli.CliFrontendParser;
import org.apache.flink.client.cli.CustomCommandLine;
import org.apache.flink.client.cli.ProgramOptions;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.runtime.OperatorIDPair;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.StateBackendLoader;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.mqjd.flink.env.Environment;
import org.mqjd.flink.env.EnvironmentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobStateReader {

    private static final Logger LOG = LoggerFactory.getLogger(JobStateReader.class);

    public void read(String jobConfig, String[] args) throws Exception {
        Environment environment = EnvironmentParser.parse(jobConfig, new String[0]);
        Configuration configuration = environment.getJobConfig().getConfiguration();
        JobGraph jobGraph = buildJobGraph(new Configuration(), args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String executionSavepointPath = configuration.get(SavepointConfigOptions.SAVEPOINT_PATH);
        StateBackend stateBackend = StateBackendLoader.loadStateBackendFromConfig(configuration,
            Thread.currentThread().getContextClassLoader(), LOG);
        SavepointReader savepoint = SavepointReader.read(env, executionSavepointPath, stateBackend);
        JobVertex vertex = jobGraph.getVertices().iterator().next();
        Iterator<OperatorIDPair> iterator = vertex.getOperatorIDs().iterator();
        OperatorIDPair operatorIDPair = (OperatorIDPair) IteratorUtils.toList(iterator).get(1);
        OperatorID operatorID = operatorIDPair.getUserDefinedOperatorID()
            .orElse(operatorIDPair.getGeneratedOperatorID());
        DataStream<Long> listState = savepoint.readListState(
            OperatorIdentifier.forUidHash(operatorID.toHexString()), "counter", Types.LONG);
        listState.printToErr("1212");
        env.execute("");
    }

    private JobGraph buildJobGraph(Configuration configuration, String[] args) throws Exception {
        List<CustomCommandLine> customCommandLines = CliFrontend.loadCustomCommandLines(
            configuration, Objects.requireNonNull(getClass().getResource("/")).getFile());
        final CliFrontend cli = new CliFrontend(configuration, customCommandLines);
        final Options commandOptions = CliFrontendParser.getRunCommandOptions();
        final CommandLine commandLine = cli.getCommandLine(commandOptions, args, true);
        final ProgramOptions programOptions = ProgramOptions.create(commandLine);
        PackagedProgram packagedProgram = PackagedProgram.newBuilder().setJarFile(null)
            .setUserClassPaths(programOptions.getClasspaths())
            .setEntryPointClassName(programOptions.getEntryPointClassName())
            .setConfiguration(configuration)
            .setSavepointRestoreSettings(programOptions.getSavepointRestoreSettings())
            .setArguments().build();
        return PackagedProgramUtils.createJobGraph(packagedProgram, configuration,
            configuration.get(CoreOptions.DEFAULT_PARALLELISM), true);
    }

}
