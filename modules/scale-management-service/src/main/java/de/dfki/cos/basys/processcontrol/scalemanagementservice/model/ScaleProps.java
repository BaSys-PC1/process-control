package de.dfki.cos.basys.processcontrol.scalemanagementservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScaleProps {
    int pieces;
    String material;
    double tare;
}
