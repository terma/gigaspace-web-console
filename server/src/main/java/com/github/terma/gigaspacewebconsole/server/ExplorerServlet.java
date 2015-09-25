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

package com.github.terma.gigaspacewebconsole.server;

import com.github.terma.gigaspacewebconsole.AppVersionValidator;
import com.github.terma.gigaspacewebconsole.CachedProviderResolver;
import com.github.terma.gigaspacewebconsole.JsonServlet;
import com.github.terma.gigaspacewebconsole.Validator;
import com.github.terma.gigaspacewebconsole.core.CopyRequest;
import com.github.terma.gigaspacewebconsole.core.ExploreRequest;

public class ExplorerServlet extends JsonServlet<ExploreRequest> {

    @Override
    protected Object doJson(ExploreRequest request) throws Exception {
        return CachedProviderResolver.getProvider(request.driver).explore(request);
    }

    @Override
    protected Class<ExploreRequest> getRequestClass() {
        return ExploreRequest.class;
    }

    @Override
    protected Validator<ExploreRequest> getValidator() {
        return new AppVersionValidator<>();
    }

}
