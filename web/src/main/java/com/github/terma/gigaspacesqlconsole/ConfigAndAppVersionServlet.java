package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.config.Config;

public class ConfigAndAppVersionServlet extends JsonServlet<Object> {

    @Override
    protected Object doJson(Object request) throws Exception {
        return Config.read();
    }

    @Override
    protected Class getRequestClass() {
        return Object.class;
    }

}
