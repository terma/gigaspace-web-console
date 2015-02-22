package com.github.terma.gigaspacesqlconsole.provider.groovy;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.provider.Executor;
import groovy.lang.Closure;

public class SqlClosure extends Closure {

    private final ExecuteRequest request;

    public SqlClosure(final ExecuteRequest request) {
        super(null);
        this.request = request;
    }

    @Override
    public Object call(Object arguments) {
        if (arguments == null) throw new NullPointerException("Can't sql null!");

        final ExecuteRequest concreteRequest = new ExecuteRequest();
        concreteRequest.gs = request.gs;
        concreteRequest.url = request.url;
        concreteRequest.user = request.user;
        concreteRequest.password = request.password;

        concreteRequest.sql = arguments.toString();

        try {
            return Executor.execute(concreteRequest);
        } catch (Exception exception) {
            throw new RuntimeException("Can't sql!", exception);
        }
    }

}
