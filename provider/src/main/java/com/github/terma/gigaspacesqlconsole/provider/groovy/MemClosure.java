package com.github.terma.gigaspacesqlconsole.provider.groovy;

import com.gigaspaces.async.AsyncResult;
import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.provider.GigaSpaceUtils;
import groovy.lang.Closure;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.executor.DistributedTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MemClosure extends Closure {

    private final ExecuteRequest request;

    public MemClosure(final ExecuteRequest request) {
        super(null);
        this.request = request;
    }

    @Override
    public Object call() {
        final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace(request);

        List<Mem> mem;
        try {
            mem = gigaSpace.execute(new MemTask()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        final ArraySqlResultBuilder builder = new ArraySqlResultBuilder("mem",
                Arrays.asList("", "MAX", "TOTAL", "FREE", "USED"));

        long max = 0;
        long total = 0;
        long free = 0;
        for (final Mem mem1 : mem) {
            max += mem1.max;
            total += mem1.total;
            free += mem1.free;
        }
        builder.add("Total (" + mem.size() + " GSC)", longAsMb(max), longAsMb(total),
                longAsMb(free), longAsMb(total - free));

        for (final Mem mem1 : mem) {
            builder.add("", longAsMb(mem1.max), longAsMb(mem1.total),
                    longAsMb(mem1.free), longAsMb(mem1.total - mem1.free));
        }

        return builder.createResult();
    }

    private static String longAsMb(long bytes) {
        return (bytes / 1024 / 1024) + " MB";
    }

    private static class Mem implements Serializable {

        private static final long serialVersionUID = 1L;

        public final long total;
        public final long free;
        public final long max;

        private Mem(long total, long free, long max) {
            this.total = total;
            this.free = free;
            this.max = max;
        }

    }

    private static class MemTask implements DistributedTask<Mem, List<Mem>> {

        @Override
        public List<Mem> reduce(List<AsyncResult<Mem>> list) throws Exception {
            final List<Mem> result = new ArrayList<>();
            for (AsyncResult<Mem> partialResult : list) {
                result.add(partialResult.getResult());
            }
            return result;
        }

        @Override
        public Mem execute() throws Exception {
            final Runtime rt = Runtime.getRuntime();
            rt.gc();
            Thread.sleep(5000);
            return new Mem(rt.totalMemory(), rt.freeMemory(), rt.maxMemory());
        }

    }

}
