package com.depscanner.notificationservice.event;

import lombok.*;

/**
 * DTO representing a dependency.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DependencyDto {
    private String name;
    private String version;
    private String system;
}
