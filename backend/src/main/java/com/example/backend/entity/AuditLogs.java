package com.example.backend.entity;

import com.example.backend.constant.enums.AuditActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "audit_logs")
public class AuditLogs extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private AuditActionType actionType;

    @Column(name = "target_entity")
    private String targetEntity;

    @Column(name = "target_id")
    private Long targetId;
}
