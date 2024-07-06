package org.mqjd.flink.jobs.chapter3.section2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.FlinkPipelineTranslationUtil;
import org.apache.flink.client.cli.CliFrontend;
import org.apache.flink.client.cli.CliFrontendParser;
import org.apache.flink.client.cli.CustomCommandLine;
import org.apache.flink.client.cli.ProgramOptions;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.core.fs.CloseableRegistry;
import org.apache.flink.runtime.OperatorIDPair;
import org.apache.flink.runtime.checkpoint.OperatorState;
import org.apache.flink.runtime.checkpoint.OperatorSubtaskState;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.state.KeyGroupRange;
import org.apache.flink.runtime.state.KeyGroupRangeAssignment;
import org.apache.flink.runtime.state.KeyedStateBackend;
import org.apache.flink.runtime.state.KeyedStateBackendParametersImpl;
import org.apache.flink.runtime.state.OperatorStateBackend;
import org.apache.flink.runtime.state.OperatorStateBackendParametersImpl;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.StateBackend.KeyedStateBackendParameters;
import org.apache.flink.runtime.state.StateBackendLoader;
import org.apache.flink.runtime.state.ttl.TtlTimeProvider;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.state.api.runtime.metadata.SavepointMetadataV2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.streaming.api.graph.StreamNode;
import org.mqjd.flink.env.Environment;
import org.mqjd.flink.jobs.chapter3.section2.core.StateEnvironment;
import org.mqjd.flink.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobStateContext {

    private static final Logger LOG = LoggerFactory.getLogger(JobStateContext.class);

    private final StateEnvironment environment = new StateEnvironment();
    private final PackagedProgram packagedProgram;
    private final StreamGraph streamGraph;
    private final JobGraph jobGraph;
    private final StateBackend stateBackend;
    private final SavepointMetadataV2 savepointMetadata;
    private final JobStateBackend jobStateBackend;

    public JobStateContext(PackagedProgram packagedProgram, StreamGraph streamGraph,
        JobGraph jobGraph, StateBackend stateBackend, SavepointMetadataV2 savepointMetadata) {
        this.packagedProgram = packagedProgram;
        this.streamGraph = streamGraph;
        this.jobGraph = jobGraph;
        this.stateBackend = stateBackend;
        this.savepointMetadata = savepointMetadata;
        this.jobStateBackend = createJobStateBackend();
    }

    public JobID getJobId() {
        return jobGraph.getJobID();
    }

    public JobStateBackend getJobStateBackend() {
        return jobStateBackend;
    }

    public static JobStateContext create(Environment environment, String[] args) throws Exception {
        Configuration configuration = environment.getJobConfig().getConfiguration();
        PackagedProgram packagedProgram = buildPackagedProgram(configuration, args);
        StreamGraph streamGraph = buildPipeline(packagedProgram, configuration);
        JobGraph jobGraph = buildJobGraph(streamGraph, packagedProgram, configuration);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String executionSavepointPath = configuration.get(SavepointConfigOptions.SAVEPOINT_PATH);
        StateBackend stateBackend = StateBackendLoader.loadStateBackendFromConfig(configuration,
            Thread.currentThread().getContextClassLoader(), LOG);
        SavepointReader savepointReader = SavepointReader.read(env, executionSavepointPath,
            stateBackend);
        SavepointMetadataV2 savepointMetadata = ReflectionUtil.read(savepointReader, "metadata");
        return new JobStateContext(packagedProgram, streamGraph, jobGraph, stateBackend,
            savepointMetadata);
    }

    private JobStateBackend createJobStateBackend() {
        JobStateBackend jobStateBackend = new JobStateBackend();
        for (VertexDescription description : analyzeVertex()) {
            VertexStateBackend vertexStateBackend = new VertexStateBackend(description);
            OperatorState operatorState = getVertexOperatorState(description);
            operatorState.getSubtaskStates().forEach(
                (key, value) -> vertexStateBackend.addSubtaskStateBackend(
                    new SubtaskStateBackend(description, key,
                        createOperatorStateBackend(description, value),
                        createKeyedStateBackend(description, key, value))));
            jobStateBackend.addVertexStateBackend(vertexStateBackend);
        }
        return jobStateBackend;
    }

    private OperatorState getVertexOperatorState(VertexDescription vertexDescription) {
        try {
            return savepointMetadata.getOperatorState(vertexDescription.getOperatorIdentifier());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private KeyedStateBackend<?> createKeyedStateBackend(VertexDescription vertexDescription,
        Integer index, OperatorSubtaskState subtaskState) {
        if (vertexDescription.getKeySerializer() == null) {
            return null;
        }

        final KeyGroupRange keyGroupRange = KeyGroupRangeAssignment.computeKeyGroupRangeForOperatorIndex(
            vertexDescription.getMaxParallelism(), vertexDescription.getParallelism(), index);
        try {
            return stateBackend.createKeyedStateBackend(
                new KeyedStateBackendParametersImpl<>(environment, getJobId(),
                    vertexDescription.getOperatorIdentifier().toString(),
                    vertexDescription.getKeySerializer(), vertexDescription.getMaxParallelism(),
                    keyGroupRange, null, TtlTimeProvider.DEFAULT, null,
                    subtaskState.getManagedKeyedState(), new CloseableRegistry()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private OperatorStateBackend createOperatorStateBackend(VertexDescription vertexDescription,
        OperatorSubtaskState subtaskState) {
        try {
            return stateBackend.createOperatorStateBackend(
                new OperatorStateBackendParametersImpl(environment,
                    vertexDescription.getOperatorID().getGeneratedOperatorID().toHexString(),
                    subtaskState.getManagedOperatorState(), new CloseableRegistry()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static PackagedProgram buildPackagedProgram(Configuration configuration, String[] args)
        throws Exception {
        List<CustomCommandLine> customCommandLines = CliFrontend.loadCustomCommandLines(
            configuration,
            Objects.requireNonNull(JobStateContext.class.getResource("/")).getFile());
        final CliFrontend cli = new CliFrontend(configuration, customCommandLines);
        final Options commandOptions = CliFrontendParser.getRunCommandOptions();
        final CommandLine commandLine = cli.getCommandLine(commandOptions, args, true);
        final ProgramOptions programOptions = ProgramOptions.create(commandLine);
        return PackagedProgram.newBuilder().setJarFile(null)
            .setUserClassPaths(programOptions.getClasspaths())
            .setEntryPointClassName(programOptions.getEntryPointClassName())
            .setConfiguration(configuration)
            .setSavepointRestoreSettings(programOptions.getSavepointRestoreSettings())
            .setArguments().build();
    }

    private static StreamGraph buildPipeline(PackagedProgram packagedProgram,
        Configuration configuration) throws Exception {
        return (StreamGraph) PackagedProgramUtils.getPipelineFromProgram(packagedProgram,
            configuration, configuration.get(CoreOptions.DEFAULT_PARALLELISM), true);
    }

    private static JobGraph buildJobGraph(StreamGraph streamGraph, PackagedProgram packagedProgram,
        Configuration configuration) {
        final JobGraph jobGraph = FlinkPipelineTranslationUtil.getJobGraphUnderUserClassLoader(
            packagedProgram.getUserCodeClassLoader(), streamGraph, configuration,
            configuration.get(CoreOptions.DEFAULT_PARALLELISM));
        jobGraph.addJars(packagedProgram.getJobJarAndDependencies());
        jobGraph.setClasspaths(packagedProgram.getClasspaths());
        jobGraph.setSavepointRestoreSettings(packagedProgram.getSavepointSettings());
        return jobGraph;
    }

    private List<VertexDescription> analyzeVertex() {
        List<VertexDescription> result = new ArrayList<>();
        int index = 0;
        List<OperatorIDPair> idPairs = findAllOperatorIDPairs(jobGraph);
        for (StreamNode streamNode : streamGraph.getStreamNodes()) {
            VertexDescription vertexDescription = new VertexDescription(streamNode,
                idPairs.get(index++));
            result.add(vertexDescription);
        }
        return result;
    }

    private List<OperatorIDPair> findAllOperatorIDPairs(JobGraph jobGraph) {
        List<OperatorIDPair> idPairs = new ArrayList<>();
        for (JobVertex vertex : jobGraph.getVertices()) {
            idPairs.addAll(vertex.getOperatorIDs());
        }
        Collections.reverse(idPairs);
        return idPairs;
    }

}
