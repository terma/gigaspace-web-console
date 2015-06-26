/*
Copyright 2015 Artem Stasiuk

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

package com.github.terma.gigaspacewebconsole;

import com.github.terma.gigaspacewebconsole.core.AppVersionRequest;
import com.github.terma.gigaspacewebconsole.core.config.Config;

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
