package org.mqjd.flink.jobs.chapter3.section2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.flink.api.common.functions.Function;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.StateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.source.Source;
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
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.state.*;
import org.apache.flink.shaded.guava31.com.google.common.collect.Lists;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.state.api.runtime.metadata.SavepointMetadataV2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.streaming.api.graph.StreamNode;
import org.apache.flink.streaming.api.operators.SourceOperatorFactory;
import org.apache.flink.streaming.api.operators.StreamOperatorFactory;
import org.apache.flink.streaming.api.operators.UdfStreamOperatorFactory;
import org.apache.flink.streaming.runtime.operators.sink.SinkWriterOperatorFactory;
import org.mqjd.flink.env.Environment;
import org.mqjd.flink.env.EnvironmentParser;
import org.mqjd.flink.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobStateReader {

    private static final Logger LOG = LoggerFactory.getLogger(JobStateReader.class);

    public void read(String jobConfig, String[] args) throws Exception {
        Environment environment = EnvironmentParser.parse(jobConfig, new String[0]);
        Configuration configuration = environment.getJobConfig().getConfiguration();
        List<VertexDescription> vertexDescriptions = analyzeJob(configuration, args);

        PackagedProgram packagedProgram = buildPackagedProgram(configuration, args);
        StreamGraph streamGraph = buildPipeline(packagedProgram, configuration);
        JobGraph jobGraph = buildJobGraph(streamGraph, packagedProgram, configuration);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String executionSavepointPath = configuration.get(SavepointConfigOptions.SAVEPOINT_PATH);
        StateBackend stateBackend = StateBackendLoader.loadStateBackendFromConfig(configuration,
            Thread.currentThread().getContextClassLoader(), LOG);
        SavepointReader savepointReader = SavepointReader.read(env, executionSavepointPath, stateBackend);
        SavepointMetadataV2 savepointMetadata = ReflectionUtil.read(savepointReader, "metadata");

        List<OperatorStateHandle> operatorStateHandles = savepointMetadata.getExistingOperators()
            .stream()
            .flatMap(v -> v.getStates().stream())
            .flatMap(v -> v.getManagedOperatorState().stream())
            .toList();

        try (DefaultOperatorStateBackend operatorStateBackend =
            new DefaultOperatorStateBackendBuilder(packagedProgram.getUserCodeClassLoader(),
                streamGraph.getExecutionConfig(), false, operatorStateHandles, new CloseableRegistry()).build()) {
            for (StateDescriptor<?, ?> v : vertexDescriptions.get(1).getStateDescriptors()) {
                if (v instanceof ListStateDescriptor) {
                    ListState<?> listState = operatorStateBackend.getListState((ListStateDescriptor<?>) v);
                    ArrayList<?> objects = Lists.newArrayList(listState.get());
                    System.out.println(objects);
                }
            }
        }
        List<OperatorIDPair> operatorIDPairs = findAllOperatorIDPairs(jobGraph);
        OperatorIDPair operatorIDPair = operatorIDPairs.get(1);
        OperatorID operatorID =
            operatorIDPair.getUserDefinedOperatorID().orElse(operatorIDPair.getGeneratedOperatorID());
        DataStream<Long> listState = savepointReader
            .readListState(OperatorIdentifier.forUidHash(operatorID.toHexString()), "counter", Types.LONG);
        listState.printToErr("1212");
        env.execute("");
    }

    private List<VertexDescription> analyzeJob(Configuration configuration, String[] args) throws Exception {
        PackagedProgram packagedProgram = buildPackagedProgram(configuration, args);
        StreamGraph streamGraph = buildPipeline(packagedProgram, configuration);
        JobGraph jobGraph = buildJobGraph(streamGraph, packagedProgram, configuration);
        List<VertexDescription> result = new ArrayList<>();
        int index = 0;
        List<OperatorIDPair> idPairs = findAllOperatorIDPairs(jobGraph);
        for (StreamNode streamNode : streamGraph.getStreamNodes()) {
            VertexDescription vertexDescription = new VertexDescription();
            vertexDescription.setOperatorID(idPairs.get(index++));
            StreamOperatorFactory<?> operatorFactory = streamNode.getOperatorFactory();
            Class<?> clz;
            if (operatorFactory.isStreamSource()) {
                SourceOperatorFactory<?> sourceOperatorFactory = (SourceOperatorFactory<?>) operatorFactory;
                Source<?, ?, ?> source = ReflectionUtil.read(sourceOperatorFactory, "source");
                vertexDescription.setCheckpointSerializer(source.getEnumeratorCheckpointSerializer());
                clz = source.getClass();
            } else if (operatorFactory instanceof UdfStreamOperatorFactory) {
                Function function = ((UdfStreamOperatorFactory<?>) operatorFactory).getUserFunction();
                clz = function.getClass();
                List<StateDescriptor<?, ?>> descriptors = ReflectionUtil.findAll(function, StateDescriptor.class);
                vertexDescription.setStateDescriptors(descriptors);
            } else if (operatorFactory instanceof SinkWriterOperatorFactory) {
                Sink<?> sink = ((SinkWriterOperatorFactory<?, ?>) operatorFactory).getSink();
                clz = sink.getClass();
            } else {
                continue;
            }
            vertexDescription.setName(streamNode.getOperatorName());
            vertexDescription.setOperatorClass(clz);
            result.add(vertexDescription);
        }
        return result;
    }

    private List<OperatorIDPair> findAllOperatorIDPairs(JobGraph jobGraph) {
        List<OperatorIDPair> idPairs = new ArrayList<>();
        for (JobVertex vertex : jobGraph.getVertices()) {
            idPairs.addAll(vertex.getOperatorIDs());
        }
        return idPairs.reversed();
    }

    private JobGraph buildJobGraph(StreamGraph streamGraph, PackagedProgram packagedProgram,
        Configuration configuration) {
        final JobGraph jobGraph =
            FlinkPipelineTranslationUtil.getJobGraphUnderUserClassLoader(packagedProgram.getUserCodeClassLoader(),
                streamGraph, configuration, configuration.get(CoreOptions.DEFAULT_PARALLELISM));
        jobGraph.addJars(packagedProgram.getJobJarAndDependencies());
        jobGraph.setClasspaths(packagedProgram.getClasspaths());
        jobGraph.setSavepointRestoreSettings(packagedProgram.getSavepointSettings());
        return jobGraph;
    }

    private PackagedProgram buildPackagedProgram(Configuration configuration, String[] args) throws Exception {
        List<CustomCommandLine> customCommandLines = CliFrontend.loadCustomCommandLines(configuration,
            Objects.requireNonNull(getClass().getResource("/")).getFile());
        final CliFrontend cli = new CliFrontend(configuration, customCommandLines);
        final Options commandOptions = CliFrontendParser.getRunCommandOptions();
        final CommandLine commandLine = cli.getCommandLine(commandOptions, args, true);
        final ProgramOptions programOptions = ProgramOptions.create(commandLine);
        return PackagedProgram.newBuilder()
            .setJarFile(null)
            .setUserClassPaths(programOptions.getClasspaths())
            .setEntryPointClassName(programOptions.getEntryPointClassName())
            .setConfiguration(configuration)
            .setSavepointRestoreSettings(programOptions.getSavepointRestoreSettings())
            .setArguments()
            .build();
    }

    private StreamGraph buildPipeline(PackagedProgram packagedProgram, Configuration configuration) throws Exception {
        return (StreamGraph) PackagedProgramUtils.getPipelineFromProgram(packagedProgram, configuration,
            configuration.get(CoreOptions.DEFAULT_PARALLELISM), true);
    }

}
