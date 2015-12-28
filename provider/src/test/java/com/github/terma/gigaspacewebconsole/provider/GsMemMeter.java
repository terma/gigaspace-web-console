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

package com.github.terma.gigaspacewebconsole.provider;

import com.gigaspaces.async.AsyncResult;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.executor.DistributedTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class GsMemMeter {

    private final GigaSpace gigaSpace;
    private final long before;

    public GsMemMeter(GigaSpace gigaSpace) {
        this.gigaSpace = gigaSpace;
        before = getUsedNow(gigaSpace);
    }

    private static long getUsedNow(GigaSpace gigaSpace) {
        try {
            return gigaSpace.execute(new MemTask()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public long getUsed() {
        return Math.max(0, getUsedNow(gigaSpace) - before);
    }

    public long getUsedMb() {
        return getUsed() / 1024 / 1024;
    }

    private static class MemTask implements DistributedTask<Long, Long> {

        @Override
        public Long reduce(List<AsyncResult<Long>> list) throws Exception {
            long r = 0;
            for (AsyncResult<Long> partialResult : list) {
                r += partialResult.getResult();
            }
            return r;
        }

        @Override
        public Long execute() throws Exception {
            final Runtime rt = Runtime.getRuntime();
            rt.gc();
            rt.gc();
            Thread.sleep(500);
            return rt.totalMemory() - rt.freeMemory();
        }

    }

}
