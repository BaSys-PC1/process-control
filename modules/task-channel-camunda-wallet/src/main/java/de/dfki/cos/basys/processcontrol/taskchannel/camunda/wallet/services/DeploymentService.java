package de.dfki.cos.basys.processcontrol.taskchannel.camunda.wallet.services;

import de.dfki.cos.basys.common.rest.camunda.ApiException;
import de.dfki.cos.basys.common.rest.camunda.api.DeploymentApi;
import de.dfki.cos.basys.common.rest.camunda.dto.DeploymentDto;
import de.dfki.cos.basys.common.rest.camunda.dto.DeploymentWithDefinitionsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.threeten.bp.OffsetDateTime;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeploymentService {

    @Value("${camunda.processDeployer.watchedPath:#dont_watch#}")
    private String watchedPath;

    @Value("${camunda.processDeployer.fileSuffixes}")
    private String fileSuffixes;

    @Value("${camunda.processDeployer.recursive}")
    private boolean recursive;

    @Autowired
    private final DeploymentApi api;

    @Autowired
    private final WatchService watchService;

    private Map<String,String> fileToIdMap = new HashMap<>();

    @PostConstruct
    public void recreateDeployments() {
        clearDeployments();
        for (String filePath : getFilePaths()) {
            String id = createDeployment(filePath);
            if (id != null) {
                fileToIdMap.put(filePath, id);
            }
        }
    }

    @Async()
    public void startMonitoring() {
        log.info("START_MONITORING");
        Path path = Paths.get(watchedPath);
        try {
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> ev : key.pollEvents()) {
                    log.debug("Event kind: {}; File affected: {}", ev.kind(), ev.context());
                    Path name = (Path) ev.context();
                    Path filePath = path.resolve(name);
                    if (Files.isDirectory(filePath)) {
                        log.debug(ev.kind() + " -> " + filePath.toString() + " <dir>");
                    } else {
                        log.debug(ev.kind() + " -> " + filePath.toString() + " <file>");
                        if (ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            String id = createDeployment(filePath.toString());
                            if (id != null) {
                                fileToIdMap.put(filePath.toString(), id);
                            }
                        }
                        else if (ev.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            String id = fileToIdMap.remove(filePath.toString());
                            if (id != null) {
                               deleteDeployment(id);
                            }
                        }
                        else if (ev.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            String id = fileToIdMap.remove(filePath.toString());
                            if (id != null) {
                                deleteDeployment(id);
                            }
                            id = createDeployment(filePath.toString());
                            if (id != null) {
                                fileToIdMap.put(filePath.toString(), id);
                            }
                        }
                        else {
                            //unknown
                        }

                    }
                }
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            log.warn("interrupted exception for monitoring service");
        }
    }

    @PreDestroy
    public void stopMonitoring() {
        log.info("STOP_MONITORING");

        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.error("exception while closing the monitoring service");
            }
        }
    }

    public List<String> getFilePaths() {
        String[] suffixes = fileSuffixes.split(" ");
        FileFilter filter = new SuffixFileFilter(suffixes);

        List<String> paths = new LinkedList<String>();

        int depth = 1;
        if (recursive)
            depth = Integer.MAX_VALUE;

        try {
            Files.find(Paths.get(watchedPath), depth, (filePath, fileAttr) -> filter.accept(filePath.toFile())).map(p -> p.toString()).forEach(paths::add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return paths;
    }

    public String createDeployment(String filePath) {
        log.info("create deployment " + filePath);

        String tenantId = null;
        String deploymentSource = "BaSys Process Deployer";
        Boolean deployChangedOnly = null;
        Boolean enableDuplicateFiltering = null;
        String deploymentName = FilenameUtils.getBaseName(filePath);
        try {
            DeploymentWithDefinitionsDto response = api.createDeployment(tenantId, deploymentSource, deployChangedOnly, enableDuplicateFiltering, deploymentName, new File(filePath));
            return response.getId();
        } catch (ApiException e) {
            log.error("could not create deployment " + filePath);
            log.error(e.getMessage());
        }
        return null;
    }

    public void deleteDeployment(String id) {
        log.info("delete deployment " + id);

        Boolean cascade = true;
        Boolean skipCustomListeners = null;
        Boolean skipIoMappings = null;
        try {
            api.deleteDeployment(id, cascade, skipCustomListeners, skipIoMappings);
        } catch (ApiException e) {
            log.error("could not delete deployment " + id);
            log.error(e.getMessage());
        }
    }

    public void clearDeployments() {
        log.info("clear deployments");

        String id = null;
        String name = null;
        String nameLike = null;
        String source = null;
        Boolean withoutSource = null;
        String tenantIdIn = null;
        Boolean withoutTenantId = null;
        Boolean includeDeploymentsWithoutTenantId = null;
        OffsetDateTime after = null;
        OffsetDateTime before = null;
        String sortBy = null;
        String sortOrder = null;
        Integer firstResult = null;
        Integer maxResults = null;
        try {
            List<DeploymentDto> response = api.getDeployments(id, name, nameLike, source, withoutSource, tenantIdIn, withoutTenantId, includeDeploymentsWithoutTenantId, after, before, sortBy, sortOrder, firstResult, maxResults);

            for (DeploymentDto deployment : response) {
                deleteDeployment(deployment.getId());
            }
        } catch (ApiException e) {
            log.error("could not clear deployments");
            log.error(e.getMessage());
        }

    }

}
