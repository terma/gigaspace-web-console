package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ExecuteResponseStream;

import java.io.IOException;

public interface ExecutorPlugin {

    boolean execute(ExecuteRequest request, ExecuteResponseStream responseStream) throws IOException;

}
