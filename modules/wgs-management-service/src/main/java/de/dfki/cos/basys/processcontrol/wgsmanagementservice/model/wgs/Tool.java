package de.dfki.cos.basys.processcontrol.wgsmanagementservice.model.wgs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tool {
    private String name;
    private String url;
}
