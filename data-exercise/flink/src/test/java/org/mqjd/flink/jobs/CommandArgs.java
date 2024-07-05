package org.mqjd.flink.jobs;

import java.util.ArrayList;
import java.util.List;

public class CommandArgs {

    private String defaultKey;

    private final List<String> options = new ArrayList<>();

    public static CommandArgs builder() {
        return new CommandArgs();
    }

    public CommandArgs defaultKey(String defaultOption) {
        this.defaultKey = "-" + defaultOption;
        return this;
    }

    public CommandArgs option(String option) {
        addDefaultOption();
        this.options.add(option);
        return this;
    }

    public void addDefaultOption() {
        if (defaultKey != null) {
            this.options.add(defaultKey);
        }
    }

    public CommandArgs options(String... options) {
        for (String option : options) {
            addDefaultOption();
            this.options.add(option);
        }
        return this;
    }

    public <T> CommandArgs option(String option, T value) {
        this.options.add(option);
        this.options.add(String.valueOf(value));
        return this;
    }

    public <T> CommandArgs kvOption(String option, String key, T value) {
        this.options.add(option);
        this.options.add(String.format("%s=%s", key, value));
        return this;
    }

    public <T> CommandArgs kvOption(String key, T value) {
        addDefaultOption();
        this.options.add(String.format("%s=%s", key, value));
        return this;
    }

    public String[] build() {
        return options.toArray(new String[0]);
    }

}
