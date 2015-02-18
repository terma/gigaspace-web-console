package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.AppVersionRequest;
import com.github.terma.gigaspacesqlconsole.core.config.Config;

import javax.servlet.ServletException;

public class AppVersionValidator<T extends AppVersionRequest> implements Validator<T> {

    private final String appVersion = Config.read().internal.appVersion;

    @Override
    public void validate(T request) throws Exception {
        if (!appVersion.equals(request.appVersion)) {
            throw new ServletException(
                    "Wow! Your console was updated to version " + appVersion
                            + ". Please refresh page (F5 or Ctrl-R) and enjoy with new features. Thx");
        }
    }

}
