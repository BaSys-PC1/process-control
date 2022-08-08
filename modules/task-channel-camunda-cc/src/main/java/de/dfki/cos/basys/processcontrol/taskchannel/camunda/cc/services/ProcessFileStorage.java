package de.dfki.cos.basys.processcontrol.taskchannel.camunda.cc.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class ProcessFileStorage {

	private Map<String, String> filePathToIdMap = new HashMap<>(); 
	
	public synchronized Optional<String> replace(String relativePath, String id) {
		return Optional.ofNullable(filePathToIdMap.put(relativePath, id));
	}
	
	public synchronized Optional<String> remove(String relativePath) {
		return Optional.ofNullable(filePathToIdMap.remove(relativePath));
	}	
	
	public synchronized String[] listIds() {
		return filePathToIdMap.values().stream().flatMap(Stream::of).collect(Collectors.toSet()).toArray(new String[0]);
	}
}

