package com.github.terma.gigaspacesqlconsole.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.io.File;
import java.util.Arrays;

public class Runner {

    public static final int PORT = 8091;
    public static final String ADDRESS = "http://localhost:" + PORT;

    public static void main(String[] args) {
        new Runner().start();
    }

    private Server server;

    public void start() {
        final File currentDir = getCurrentDir();
//        final File configFile = currentDir.getParentFile().getParentFile().getParentFile();
//        Assert.assertThat(configFile.exists(), Matchers.equalTo(true));

        final File warFile = getWarFile(currentDir);

        final WebAppContext webAppContext = new WebAppContext();
        webAppContext.setWar(warFile.getAbsolutePath());

        server = new Server(PORT);
        server.setHandler(webAppContext);
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File getWarFile(File currentDir) {
        for (File file : currentDir.listFiles()) {
            if (file.getName().endsWith(".war")) {
                return file;
            }
        }
        throw new IllegalArgumentException("Can't find war file in " + Arrays.asList(currentDir.list()));
    }

    private static File getCurrentDir() {
        File currentDir = new File(".");
        System.out.println("Current dir: " + currentDir.getAbsolutePath());
        if (!currentDir.getAbsolutePath().endsWith("/server/.")) {
            currentDir = new File(currentDir, "server");
        }
        currentDir = new File(currentDir, "target").getAbsoluteFile();
        Assert.assertThat(currentDir.exists(), Matchers.equalTo(true));
        return currentDir;
    }

    public void stop() {
        try {
//            Thread.sleep(10000);
            if (server != null) server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void join() throws InterruptedException {
        server.join();
    }

}
