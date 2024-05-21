package de.dfki.cos.basys.processcontrol.wgsmanagementservice.model.wgs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private String title;
    private String description;
    private String icon;
    private String x;
    private String y;
}
