package br.com.pw2m.nfc.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "children")
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserAccount owner;

    @Column(name = "public_token", nullable = false, unique = true, length = 36)
    private String publicToken = UUID.randomUUID ().toString ();

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "person_type",
            nullable = false,
            length = 30
    )
    private PersonType personType =
            PersonType.CHILD;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "address_line", length = 255)
    private String addressLine;

    @Column(length = 120)
    private String city;

    @Column(length = 2)
    private String state;

    @Column(name = "public_enabled", nullable = false)
    private boolean publicEnabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Instant createdAt = Instant.now ();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now ();

    @PreUpdate
    void preUpdate () {
        this.updatedAt = Instant.now ();
    }

    public PersonType getPersonType () {return personType;}

    public void setPersonType (PersonType personType) {this.personType = personType;}

    public Long getId () {return id;}

    public UserAccount getOwner () {return owner;}

    public void setOwner (UserAccount owner) {this.owner = owner;}

    public String getPublicToken () {return publicToken;}

    public void setPublicToken (String publicToken) {this.publicToken = publicToken;}

    public String getFullName () {return fullName;}

    public void setFullName (String fullName) {this.fullName = fullName;}

    public LocalDate getBirthDate () {return birthDate;}

    public void setBirthDate (LocalDate birthDate) {this.birthDate = birthDate;}

    public String getBloodType () {return bloodType;}

    public void setBloodType (String bloodType) {this.bloodType = bloodType;}

    public String getPhotoUrl () {return photoUrl;}

    public void setPhotoUrl (String photoUrl) {this.photoUrl = photoUrl;}

    public String getAddressLine () {return addressLine;}

    public void setAddressLine (String addressLine) {this.addressLine = addressLine;}

    public String getCity () {return city;}

    public void setCity (String city) {this.city = city;}

    public String getState () {return state;}

    public void setState (String state) {this.state = state;}

    public boolean isPublicEnabled () {return publicEnabled;}

    public void setPublicEnabled (boolean publicEnabled) {this.publicEnabled = publicEnabled;}

    public Instant getCreatedAt () {return createdAt;}

    public Instant getUpdatedAt () {return updatedAt;}
}
