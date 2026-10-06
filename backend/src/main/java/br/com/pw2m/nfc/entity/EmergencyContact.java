package br.com.pw2m.nfc.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "emergency_contacts")
public class EmergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "relation_label", nullable = false, length = 80)
    private String relationLabel;

    @Column(nullable = false, length = 40)
    private String phone;

    @Column(length = 40)
    private String whatsapp;

    @Column(nullable = false)
    private int priority = 1;

    public Long getId() { return id; }
    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRelationLabel() { return relationLabel; }
    public void setRelationLabel(String relationLabel) { this.relationLabel = relationLabel; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}
