package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.config.Config;

import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/config")
public class ConfigServlet extends JsonServlet<Object> {

    @Override
    protected Object doJson(Object request) throws Exception {
        return Config.read();
    }

    @Override
    protected Class getRequestClass() {
        return Object.class;
    }

}
