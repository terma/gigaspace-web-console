/*
Copyright 2015-2016 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.gigaspacewebconsole.server;

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
