package com.depscanner.vulnservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "version")
public class Version {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dependency_id")
    private Dependency dependency;

    private String version;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "version_detail_id")
    private VersionDetail versionDetail;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<License> licenses;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdvisoryKey> advisoryKeys;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Link> links;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "version_related_dependency",
            joinColumns = @JoinColumn(name = "version_id"),
            inverseJoinColumns = @JoinColumn(name = "related_version_id"))
    @Builder.Default
    private List<RelatedDependency> relatedDependencies = new LinkedList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "version_edge",
            joinColumns = @JoinColumn(name = "version_id"),
            inverseJoinColumns = @JoinColumn(name = "edge_id"))
    @Builder.Default
    private List<Edge> edges = new LinkedList<>();
}
