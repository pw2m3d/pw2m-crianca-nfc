package br.com.pw2m.nfc.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "access_logs")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private NfcDevice device;

    @Column(name = "accessed_at", nullable = false, updatable = false)
    private Instant accessedAt = Instant.now();

    public Long getId() { return id; }
    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }
    public NfcDevice getDevice() { return device; }
    public void setDevice(NfcDevice device) { this.device = device; }
    public Instant getAccessedAt() { return accessedAt; }
}
