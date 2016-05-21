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

package com.github.terma.gigaspacewebconsole.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "console", requiresProject = false, threadSafe = true)
public class ConsoleMojo extends AbstractMojo {

    private static final String CONFIG_SYSTEM_PROPERTY = "gigaspacewebconsoleConfig";

    @Component
    private ArtifactFactory artifactFactory;

    @Component
    private ArtifactResolver artifactResolver;

    @Parameter(defaultValue = "${localRepository}", readonly = true)
    private ArtifactRepository localRepository;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
    private List remoteRepositories;

    @Parameter(property = "gsVersion", defaultValue = "10.0.1-11800-RELEASE")
    private String gsVersion;

    @Parameter(property = CONFIG_SYSTEM_PROPERTY, alias = "config", defaultValue = "local")
    private String configPath;

    @Parameter(property = "port", defaultValue = "7777")
    private int port;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting gigaspace-web-console...");

        final String pluginVersion;
        try {
            pluginVersion = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/plugin.version"))).readLine();
        } catch (IOException e) {
            throw new MojoExecutionException("Can't read plugin version!", e);
        }

        System.setProperty(CONFIG_SYSTEM_PROPERTY, configPath);

        getLog().info("plugin version: " + pluginVersion);
        getLog().info("gsVersion: " + gsVersion);
        getLog().info(CONFIG_SYSTEM_PROPERTY + ": " + configPath);
        getLog().info("console port: " + port);

        getLog().info("Resolving dependencies...");
        List<Artifact> gsArtifacts = new ArrayList<>();
        gsArtifacts.add(resolveArtifact(artifactFactory, "com.gigaspaces", "gs-openspaces", gsVersion, "jar"));
        gsArtifacts.add(resolveArtifact(artifactFactory, "com.gigaspaces", "gs-runtime", gsVersion, "jar"));
        gsArtifacts.add(resolveArtifact(artifactFactory, "commons-logging", "commons-logging", "1.1.1", "jar"));
        gsArtifacts.add(resolveArtifact(artifactFactory, "org.springframework", "spring-core", "3.2.4.RELEASE", "jar"));
        gsArtifacts.add(resolveArtifact(artifactFactory, "org.springframework", "spring-context", "3.2.4.RELEASE", "jar"));
        gsArtifacts.add(resolveArtifact(artifactFactory, "org.springframework", "spring-tx", "3.2.4.RELEASE", "jar"));
        gsArtifacts.add(resolveArtifact(artifactFactory, "org.springframework", "spring-beans", "3.2.4.RELEASE", "jar"));
        final Artifact serverArtifact = resolveArtifact(artifactFactory,
                "com.github.terma.gigaspace-web-console", "server", pluginVersion, "war");

        final ClassRealm realm;
        try {
            realm = new ClassWorld().newRealm("gigaspace-web-console", Thread.currentThread().getContextClassLoader());
            for (Artifact artifact : gsArtifacts) realm.addConstituent(artifact.getFile().toURL());
        } catch (DuplicateRealmException | MalformedURLException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        Thread.currentThread().setContextClassLoader(realm.getClassLoader());

        runServer(serverArtifact);
    }

    private void runServer(Artifact serverArtifact) {
        getLog().info("Starting console...");

        final WebAppContext webAppContext = new WebAppContext();
//        webAppContext.setExtractWAR(false);
        webAppContext.setThrowUnavailableOnStartupException(true);
        webAppContext.setWar(serverArtifact.getFile().getAbsolutePath());

        final Server server = new Server(port);
        server.setHandler(webAppContext);
        try {
            server.start();

            getLog().info("ready and waiting you at http://localhost:" + port);

            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resolveArtifact(Artifact serverArtifact) throws MojoExecutionException {
        try {
            artifactResolver.resolve(serverArtifact, remoteRepositories, localRepository);
        } catch (ArtifactResolutionException | ArtifactNotFoundException e) {
            throw new MojoExecutionException("", e);
        }
    }

    private Artifact resolveArtifact(
            ArtifactFactory artifactFactory,
            String groupId, String artifactId, String version, String packaging) throws MojoExecutionException {
        final Artifact artifact = artifactFactory.createBuildArtifact(groupId, artifactId, version, packaging);
        resolveArtifact(artifact);
        return artifact;
    }

}
