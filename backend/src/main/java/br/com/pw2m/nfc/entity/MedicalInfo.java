package br.com.pw2m.nfc.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "medical_info")
public class MedicalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "child_id", nullable = false, unique = true)
    private Child child;

    @Lob
    private String allergies;

    @Lob
    @Column(name = "conditions_text")
    private String conditions;

    @Lob
    private String medications;

    @Column(name = "health_plan", length = 160)
    private String healthPlan;

    @Column(name = "health_plan_number", length = 120)
    private String healthPlanNumber;

    @Lob
    @Column(name = "emergency_notes")
    private String emergencyNotes;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }
    public String getMedications() { return medications; }
    public void setMedications(String medications) { this.medications = medications; }
    public String getHealthPlan() { return healthPlan; }
    public void setHealthPlan(String healthPlan) { this.healthPlan = healthPlan; }
    public String getHealthPlanNumber() { return healthPlanNumber; }
    public void setHealthPlanNumber(String healthPlanNumber) { this.healthPlanNumber = healthPlanNumber; }
    public String getEmergencyNotes() { return emergencyNotes; }
    public void setEmergencyNotes(String emergencyNotes) { this.emergencyNotes = emergencyNotes; }
}
