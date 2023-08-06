package com.depscanner.vulnservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "advisory_key")
public class AdvisoryKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String advisoryId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AdvisoryDetail advisoryDetail;
}