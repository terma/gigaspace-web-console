package com.github.terma.gigaspacewebconsole.provider.executor;

import com.github.terma.gigaspacewebconsole.provider.ConverterHelper;

import java.util.ArrayList;

public class DatabaseExecutor {

    public static final Executor INSTANCE = new Executor(
            new DatabaseConnectionFactory(),
            new ZeroExecutorPreprocessor(),
            new ArrayList<ExecutorPlugin>(),
            new ConverterHelper(new ArrayList<String>()));

}
