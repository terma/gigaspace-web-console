package com.github.terma.gigaspacesqlconsole;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NoCacheFilter implements Filter {

    public void destroy() {
    }

    public void doFilter(final ServletRequest req, final ServletResponse response, final FilterChain chain)
            throws ServletException, IOException {
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader("cache-control", "no-store");
        httpResponse.addHeader("expires", "Tue, 21 May 1984 1:00:00 GMT");
        httpResponse.addHeader("pragma", "no-cache");

        chain.doFilter(req, response);
    }

    public void init(FilterConfig config) throws ServletException {
    }

}
