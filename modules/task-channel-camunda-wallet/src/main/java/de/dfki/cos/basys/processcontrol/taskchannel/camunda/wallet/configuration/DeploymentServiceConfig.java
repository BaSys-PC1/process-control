package de.dfki.cos.basys.processcontrol.taskchannel.camunda.wallet.configuration;

import de.dfki.cos.basys.common.rest.camunda.api.DeploymentApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
//@Configuration
public class DeploymentServiceConfig {

    @Value("${camunda.processDeployer.watchedPath}")
    private String watchedPath;

    @Value("${camunda.processDeployer.fileSuffixes}")
    private String fileSuffix;

    @Value("${camunda.processDeployer.recursive}")
    private boolean recursive;

    @Value("${camunda.processDeployer.endpoint}")
    private String camundaRestEndpoint;

    @Bean
    public DeploymentApi deploymentApi() {
        String endpoint = camundaRestEndpoint;
        if (endpoint.endsWith("/"))
            endpoint = endpoint.substring(0, endpoint.length()-1);

        DeploymentApi api = new DeploymentApi();
        api.getApiClient().setBasePath(endpoint);
        return api;
    }

    @Bean
    public WatchService watchService() {
        log.debug("watch folder: {}", watchedPath);
        try {
            final WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(watchedPath);

            log.info("watch folder: {}", path.toAbsolutePath());
            if (!Files.isDirectory(path)) {
                throw new RuntimeException("folder to watch is not a folder: " + path);
            }

            if (recursive) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        dir.register(
                                watchService,
                                StandardWatchEventKinds.ENTRY_DELETE,
                                StandardWatchEventKinds.ENTRY_MODIFY,
                                StandardWatchEventKinds.ENTRY_CREATE);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                path.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE
                );
            }
            return watchService;
        } catch (IOException e) {
            log.error("could not create watch service:", e);
        }
        return null;
    }
}
