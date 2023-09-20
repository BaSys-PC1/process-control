package de.dfki.cos.basys.processcontrol.scalemanagementservice.services;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
public class KnowledgeGraphService {

    @Autowired
    private Driver driver;

    @PostConstruct
    public void initialize() {
        log.info("Total: {} nodes, MaxDuration (via Knowledge Graph): {}",this._retrieveAll().size(), this._retrieveAll().toString());
    }

    public List<String> _retrieveAll() {
        String query = "MATCH ({idShort: \"ManufacturingBoxedRaspberryPi\"} )--(p:Property {idShort: \"MaxDuration\"}) RETURN p;";

        try (Session session = driver.session()) {
            return session.run(query)
                    .list(r -> r.get("p").asNode().get("value").toString());
        }

    }

}
