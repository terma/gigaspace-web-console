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

package com.github.terma.gigaspacesqlconsole.plugin;

import com.github.terma.gigaspacesqlconsole.core.config.Config;
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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "run", requiresProject = false, threadSafe = true)
public class ConsoleRunMojo extends AbstractMojo {

    @Component
    private ArtifactFactory artifactFactory;

    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    private ArtifactRepository localRepository;

    @Component
    protected ArtifactResolver artifactResolver;

    /**
     * List of Remote Repositories used by the resolver
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    private List remoteRepositories;

    @Parameter(property = "gsVersion", defaultValue = "10.0.1-11800-RELEASE")
    private String gsVersion;

    @Parameter(property = Config.CONFIG_PATH_SYSTEM_PROPERTY, alias = "config", defaultValue = Config.LOCAL)
    private String configPath;

    @Parameter(property = "port", defaultValue = "7777")
    private int port;

    @Parameter(property = "pluginVersion")
    private String pluginVersion;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting gigaspace-sql-console...");

        getLog().info("plugin version: " + pluginVersion);
        getLog().info("gsVersion: " + gsVersion);
        getLog().info(Config.CONFIG_PATH_SYSTEM_PROPERTY + ": " + configPath);
        getLog().info("console port: " + port);

        getLog().info("Resolving gs dependencies...");
        List<Artifact> gsArtifacts = new ArrayList<>();
        gsArtifacts.add(resolveArtifact(artifactFactory, "com.gigaspaces", "gs-openspaces", gsVersion, "jar"));
        gsArtifacts.add(resolveArtifact(artifactFactory, "com.gigaspaces", "gs-runtime", gsVersion, "jar"));
        gsArtifacts.add(resolveArtifact(artifactFactory, "commons-logging", "commons-logging", "1.1.1", "jar"));
        gsArtifacts.add(resolveArtifact(artifactFactory, "org.springframework", "spring-core", "3.2.4.RELEASE", "jar"));
        gsArtifacts.add(resolveArtifact(artifactFactory, "org.springframework", "spring-context", "3.2.4.RELEASE", "jar"));
        gsArtifacts.add(resolveArtifact(artifactFactory, "org.springframework", "spring-tx", "3.2.4.RELEASE", "jar"));

        final ClassRealm realm;
        try {
            realm = new ClassWorld().newRealm("gigaspace-sql-console", Thread.currentThread().getContextClassLoader());
            for (Artifact artifact : gsArtifacts) realm.addConstituent(artifact.getFile().toURL());
        } catch (DuplicateRealmException | MalformedURLException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        Thread.currentThread().setContextClassLoader(realm.getClassLoader());

        final Artifact serverArtifact = artifactFactory.createArtifact(
                "com.github.terma.gigaspace-sql-console", "server", pluginVersion, "", "war");
        resolveArtifact(serverArtifact);

        runServer(serverArtifact);
    }

    private void runServer(Artifact serverArtifact) {
        getLog().info("Starting console...");

        final WebAppContext webAppContext = new WebAppContext();
//        webAppContext.setExtractWAR(false);
        webAppContext.setWar(serverArtifact.getFile().getAbsolutePath());

        final Server server = new Server(port);
        server.setHandler(webAppContext);
        try {
            server.start();

            getLog().info("console started (cntr-C to stop it)");
            getLog().info("goto: http://localhost:" + port);

            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resolveArtifact(Artifact serverArtifact) throws MojoExecutionException {
        try {
            artifactResolver.resolve(serverArtifact, remoteRepositories, this.localRepository);
        } catch (ArtifactResolutionException | ArtifactNotFoundException e) {
            throw new MojoExecutionException("", e);
        }
    }

    private Artifact resolveArtifact(
            ArtifactFactory artifactFactory,
            String groupId, String artifactId, String version, String type) throws MojoExecutionException {
        final Artifact artifact = artifactFactory.createArtifact(groupId, artifactId, version, "", type);
        resolveArtifact(artifact);
        return artifact;
    }

}
