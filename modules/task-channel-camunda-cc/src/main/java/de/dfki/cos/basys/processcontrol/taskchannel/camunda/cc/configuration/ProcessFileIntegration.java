package de.dfki.cos.basys.processcontrol.taskchannel.camunda.cc.configuration;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileReadingMessageSource.WatchEventType;
import org.springframework.integration.file.filters.AcceptAllFileListFilter;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;
import org.springframework.integration.file.filters.ResettableFileListFilter;
import org.springframework.integration.support.DefaultMessageBuilderFactory;
import org.springframework.integration.support.MessageBuilderFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import org.camunda.community.rest.client.invoker.ApiException;
import org.camunda.community.rest.client.api.DeploymentApi;
import org.camunda.community.rest.client.dto.DeploymentDto;
import org.camunda.community.rest.client.dto.DeploymentWithDefinitionsDto;
import de.dfki.cos.basys.processcontrol.taskchannel.camunda.cc.services.ProcessFileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableIntegration
@Slf4j
public class ProcessFileIntegration {

	@Value("${camunda.processDeployer.sourcePath:#empty#}")
	private String sourcePath;

	@Value("${camunda.processDeployer.fileSuffixes:.bpmn}")
	private String fileSuffixes;

	@Value("${camunda.processDeployer.recursive:false}")
	private boolean recursive;

	@Autowired
	private DeploymentApi api;

	@Autowired
	private ProcessFileStorage storage;

	@Bean
	public MessageChannel fileUpdateChannel() {
		return newFileInputChannel();
	}

	@Bean
	public MessageChannel fileRemovalChannel() {
		return newFileInputChannel();
	}

	private MessageChannel newFileInputChannel() {
		DirectChannel channel = new DirectChannel();
		channel.setDatatypes(File.class);
		return channel;
	}

	@Bean
	@InboundChannelAdapter(value = "fileUpdateChannel")
	public MessageSource<File> fileMessageSource() {
		File directory = new File(sourcePath);
		FileReadingMessageSource sourceReader = new FileReadingMessageSource();
		sourceReader.setDirectory(directory);
		sourceReader.setUseWatchService(true);
		sourceReader.setWatchEvents(WatchEventType.DELETE, WatchEventType.CREATE, WatchEventType.MODIFY);

		String filePatternString = filePattern();
		Pattern pattern = Pattern.compile(filePatternString);
		RegexPatternFileListFilter regexFilter = new RegexPatternFileListFilter(pattern);
		FileRemovalNotificationFilter removalNotification = new FileRemovalNotificationFilter(fileRemovalChannel(), directory, pattern);

		List<FileListFilter<File>> filters = List.of(regexFilter, removalNotification);
		sourceReader.setFilter(new ChainFileListFilter<>(filters));

		return sourceReader;
	}

	private String filePattern() {
		String filePattern = getFileSuffixPattern();
		String patternPrefix = recursive ? "^.*\\." : "^[\\/]?[^\\\\/]*\\.";
		return patternPrefix + filePattern + "$";
	}

	private String getFileSuffixPattern() {
		return Stream.of(fileSuffixes.split(" ")).map(this::removeTrailingPoint).collect(Collectors.joining("|", "(", ")"));
	}

	private String removeTrailingPoint(String suffix) {
		return suffix.startsWith(".") ? suffix.substring(1) : suffix;
	}

	@ServiceActivator(inputChannel = "fileUpdateChannel")
	public void fileUpdated(@Payload File file, @Header(FileHeaders.RELATIVE_PATH) String relativePath) {
		log.info("File updated " + relativePath);
		Optional<String> idOpt = storage.remove(relativePath);
		if (idOpt.isPresent()) {
			deleteDeployment(idOpt.get());
		}
		String id = createDeployment(file.getAbsolutePath());
		if (id != null) {
			storage.replace(relativePath, id);
		}
	}

	@ServiceActivator(inputChannel = "fileRemovalChannel")
	public void fileRemoved(@Payload File file, @Header(FileHeaders.RELATIVE_PATH) String relativePath) {
		log.info("File removed " + relativePath);
		Optional<String> idOpt = storage.remove(relativePath);
		if (idOpt.isPresent()) {
			deleteDeployment(idOpt.get());
		}
	}

	private String createDeployment(String filePath) {
		log.info("create deployment " + filePath);

		String tenantId = null;
		String deploymentSource = "BaSys Process Deployer";
		Boolean deployChangedOnly = null;
		Boolean enableDuplicateFiltering = null;
		String deploymentName = FilenameUtils.getBaseName(filePath);
		Date deploymentActivationTime = null; //Date.from(Instant.now())
		try {
			DeploymentWithDefinitionsDto response = api.createDeployment(tenantId, deploymentSource, deployChangedOnly, enableDuplicateFiltering, deploymentName, deploymentActivationTime, new File(filePath));
			return response.getId();
		} catch (ApiException e) {
			log.error("could not create deployment " + filePath);
			log.error(e.getMessage());
		}
		return null;
	}

	private void deleteDeployment(String id) {
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

	private void clearDeployments() {
		log.info("clear deployments");

		String id = null;
		String name = null;
		String nameLike = null;
		String source = null;
		Boolean withoutSource = null;
		String tenantIdIn = null;
		Boolean withoutTenantId = null;
		Boolean includeDeploymentsWithoutTenantId = null;
		Date after = null;
		Date before = null;
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

	@RequiredArgsConstructor
	private static final class FileRemovalNotificationFilter extends AcceptAllFileListFilter<File> implements ResettableFileListFilter<File> {

		private final MessageChannel removalChannel;
		private final File directory;
		private final Pattern pattern;
		private final MessageBuilderFactory messageBuilderFactory = new DefaultMessageBuilderFactory();

		@Override
		public boolean remove(File file) {
			String path = file.getAbsolutePath();
			if (pattern.matcher(path).find()) {
				String relativePath = path.replaceFirst(Matcher.quoteReplacement(directory.getAbsolutePath() + File.separator), "");
				Message<File> msg = messageBuilderFactory.withPayload(file).setHeader(FileHeaders.RELATIVE_PATH, relativePath).build();
				removalChannel.send(msg);
				return true;
			}
			return false;
		}
	}
}
