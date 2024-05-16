package org.mqjd.flink.common.config;

import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.TextNode;
import org.mqjd.flink.util.YamlUtil;

public class EnvironmentParser {

    static final Option DYNAMIC_PROPERTIES = Option.builder("D").argName("property=value")
        .numberOfArgs(2).valueSeparator('=').build();

    public static Environment parse(String configPath, String[] args) {
        try {
            final DefaultParser parser = new DefaultParser();
            Options options = new Options();
            options.addOption(DYNAMIC_PROPERTIES);
            CommandLine commandLine = parser.parse(options, args);
            Properties optionProperties = commandLine.getOptionProperties(DYNAMIC_PROPERTIES);
            Environment environment = YamlUtil.fromYaml(
                EnvironmentParser.class.getClassLoader().getResource(configPath),
                Environment.class);
            environment.merge(YamlUtil.fromProperties(optionProperties, Environment.class, node -> {
                if (node.has("source")) {
                    node.withObject("/source")
                        .set("type", new TextNode(environment.getSource().getType()));
                }
                if (node.has("sink")) {
                    node.withObject("/sink")
                        .set("type", new TextNode(environment.getSink().getType()));
                }
            }));
            return environment;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
