package com.depscanner.notificationservice.event;

import lombok.*;

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
