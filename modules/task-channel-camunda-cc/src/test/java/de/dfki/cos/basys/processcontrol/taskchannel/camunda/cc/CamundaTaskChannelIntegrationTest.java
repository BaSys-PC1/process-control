package de.dfki.cos.basys.processcontrol.taskchannel.camunda.cc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import org.camunda.community.rest.client.invoker.ApiException;
import org.camunda.community.rest.client.api.DeploymentApi;
import org.camunda.community.rest.client.dto.CountResultDto;
//import org.camunda.community.rest.client.dto.DeploymentDto;
import org.camunda.community.rest.client.dto.DeploymentResourceDto;
import de.dfki.cos.basys.processcontrol.taskchannel.camunda.cc.services.ProcessFileStorage;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "camunda.processDeployer.recursive=true" })
@RunWith(SpringRunner.class)
@Log
public class CamundaTaskChannelIntegrationTest {

	private static final String NEW_FILE_BPMN = "new_file.bpmn";

	private static final String TEST_MIR100_BPMN = "testMir100.bpmn";

	private static final String BOOTSTRAP_SERVERS = "PLAINTEXT://kafka:9092";

	private static final DockerImageName KAFKA_TEST_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:6.2.1");

	private static final DockerImageName CAMUNDA_TEST_IMAGE = DockerImageName.parse("camunda/camunda-bpm-platform:7.16.0");
	//
	private static final DockerImageName SCHEMA_REGISTRY_TEST_IMAGE = DockerImageName.parse("confluentinc/cp-schema-registry:6.2.1");

	private static Network NETWORK = Network.newNetwork();

	@Autowired
	private DeploymentApi api;

	@Autowired
	private ProcessFileStorage storage;

	@ClassRule
	public static KafkaContainer KAFKA = new KafkaContainer(KAFKA_TEST_IMAGE).withNetwork(NETWORK).withNetworkAliases("kafka");

	@ClassRule
	public static GenericContainer<?> SCHEMA_REGISTRY = new GenericContainer<>(SCHEMA_REGISTRY_TEST_IMAGE).dependsOn(KAFKA).withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", BOOTSTRAP_SERVERS).withExposedPorts(8081)
			.withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081").withEnv("SCHEMA_REGISTRY_HOST_NAME", "localhost").withNetwork(NETWORK).withNetworkAliases("schema-registry");

	@ClassRule
	public static GenericContainer<?> CAMUNDA = new GenericContainer<>(CAMUNDA_TEST_IMAGE).withExposedPorts(8080).withNetwork(NETWORK).withNetworkAliases("camunda")
			.waitingFor(new HttpWaitStrategy().forPath("/engine-rest/external-task").forStatusCode(200).withStartupTimeout(Duration.ofMinutes(5)))
	// .waitingFor(new
	// HttpWaitStrategy().forPath("/engine-rest/external-task/fetchAndLock").forStatusCode(200).withStartupTimeout(Duration.ofMinutes(4)));
	;

	static {
		// just a high timeout here that would allow debugging
		Awaitility.setDefaultTimeout(Duration.ofMinutes(5));
		try {
			CamundaTaskChannelIntegrationTest.deleteTestFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@DynamicPropertySource
	static void assignAdditionalProperties(DynamicPropertyRegistry registry) throws InterruptedException {
		registry.add("kafka.bootstrap-servers", KAFKA::getBootstrapServers);
		registry.add("spring.cloud.stream.kafka.binder.brokers", CamundaTaskChannelIntegrationTest::broker);
		registry.add("camunda.processDeployer.endpoint", CamundaTaskChannelIntegrationTest::camundaEndpoint);
		registry.add("camunda.bpm.client.base_url:", CamundaTaskChannelIntegrationTest::camundaEndpoint);
		registry.add("spring.cloud.stream.binders.kafka-avro.environment.spring.cloud.stream.kafka.binder.producer-properties.schema.registry.url", CamundaTaskChannelIntegrationTest::schemaRegistryUrl);
		registry.add("spring.cloud.stream.binders.kafka-avro.environment.spring.cloud.stream.kafka.binder.consumer-properties.schema.registry.url", CamundaTaskChannelIntegrationTest::schemaRegistryUrl);
		registry.add("camunda.processDeployer.sourcePath", CamundaTaskChannelIntegrationTest::processSourcePath);
	}

	private static String broker() {
		System.out.println(KAFKA.getHost());
		System.out.println(KAFKA.getBootstrapServers());
		return KAFKA.getHost() + ":" + KAFKA.getMappedPort(KafkaContainer.KAFKA_PORT);
	}

	private static String camundaEndpoint() {
		return "http://" + CAMUNDA.getHost() + ":" + CAMUNDA.getFirstMappedPort() + "/engine-rest";
	}

	private static String schemaRegistryUrl() {
		return "http://" + SCHEMA_REGISTRY.getHost() + ":" + SCHEMA_REGISTRY.getMappedPort(8081);
	}

	private static String processSourcePath() {
		return "./src/test/resources/processes";
	}

	@Test
	@Ignore
	public void testFileIntegration() throws InterruptedException, ApiException, IOException {
		log.info("testFileIntegration()");
		// await test .bpmn file is deployed
		Awaitility.await().until(hasStorageSize(1));
		String[] ids = storage.listIds();

		// check if the current file is deployed
		String initFileId = assertThatTheCurrentFileIsDeployed(ids);
		String newFileId;
		try {
			createTestFile();
			newFileId = assertFileIsDeployed(ids, initFileId);
		} finally {
			CamundaTaskChannelIntegrationTest.deleteTestFile();
		}
		assertFileIsRemoved(initFileId, newFileId);
	}

	private void assertFileIsRemoved(String initFileId, String newId) throws ApiException {
		log.info("assertFileIsRemoved()");
		Awaitility.await().until(hasStorageSize(1));
		String[] ids = storage.listIds();
		Assert.assertEquals(initFileId, ids[0]);
		CountResultDto testDeployments = api.getDeploymentsCount(newId, null, null, null, null, 
				null, null, null, null, null);
		Assert.assertEquals(0, testDeployments.getCount().longValue());
	}

	private String assertFileIsDeployed(String[] ids, String initFileId) throws ApiException {
		log.info("assertFileIsDeployed()");
		Awaitility.await().until(hasStorageSize(2));
		ids = storage.listIds();
		HashSet<String> deployedIds = new HashSet<>(List.of(ids));
		deployedIds.remove(initFileId);
		String newId = deployedIds.iterator().next();
		List<DeploymentResourceDto> testDeployments = api.getDeploymentResources(newId);
		Assert.assertEquals(1, testDeployments.size());
		Assert.assertEquals(NEW_FILE_BPMN, testDeployments.get(0).getName());
		return newId;
	}

	private String assertThatTheCurrentFileIsDeployed(String[] ids) throws ApiException {
		log.info("assertThatTheCurrentFileIsDeployed()");
		Assert.assertEquals(1, ids.length);
		String initFileId = ids[0];
		List<DeploymentResourceDto> deployments = api.getDeploymentResources(initFileId);
		Assert.assertEquals(1, deployments.size());
		Assert.assertEquals(TEST_MIR100_BPMN, deployments.get(0).getName());
		return initFileId;
	}

	private void createTestFile() throws IOException {
		log.info("createTestFile");
		// create a copy
		Path source = Path.of(processSourcePath(), TEST_MIR100_BPMN);
		Path subFolder = Path.of(processSourcePath(), "sub");
		subFolder = Files.createDirectories(subFolder);

		Path testFilePath = subFolder.resolve(NEW_FILE_BPMN);
		Files.copy(source, testFilePath, StandardCopyOption.REPLACE_EXISTING);
	}

	private static void deleteTestFile() throws IOException {
		log.info("deleteTestFile");
		Path subFolder = Path.of(processSourcePath(), "sub");
		Path testFilePath = subFolder.resolve(NEW_FILE_BPMN);
		testFilePath = testFilePath.toAbsolutePath();
		Files.deleteIfExists(testFilePath);
		Files.deleteIfExists(subFolder);
	}

	private Callable<Boolean> hasStorageSize(int count) {
		log.info("hasStorageSize " + count);
		return () -> storage.listIds().length == count;
	}

}