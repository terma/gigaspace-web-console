package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.config.Config;

import javax.servlet.ServletException;

public abstract class JsonWithCorrectAppVersionServlet<T extends AppVersionRequest> extends JsonServlet<T> {

    private final String appVersion = Config.read().internal.appVersion;

    @Override
    protected Object doJson(final T request) throws Exception {
        if (!appVersion.equals(request.appVersion)) {
            throw new ServletException(
                    "Wow! Your console was updated to version " + appVersion
                            + ". Please refresh page (F5 or Ctrl-R) and enjoy with new features. Thx");
        }

        return doJsonWithCorrectAppVersion(request);
    }

    protected abstract Object doJsonWithCorrectAppVersion(T request) throws Exception;

}
