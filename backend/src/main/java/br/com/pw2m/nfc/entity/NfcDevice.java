package br.com.pw2m.nfc.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "nfc_devices")
public class NfcDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Column(name = "device_name", nullable = false, length = 120)
    private String deviceName;

    @Column(name = "device_token", nullable = false, unique = true, length = 36)
    private String deviceToken = UUID.randomUUID().toString();

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NfcDeviceCategory category = NfcDeviceCategory.OUTRO;

    public Long getId() { return id; }
    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getDeviceToken() { return deviceToken; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public NfcDeviceCategory getCategory() { return category;  }
    public void setCategory( NfcDeviceCategory category ) { this.category = category; }

}
