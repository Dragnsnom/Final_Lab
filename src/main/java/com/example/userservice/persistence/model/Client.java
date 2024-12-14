package com.example.userservice.persistence.model;

import com.example.userservice.app.enums.ClientStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "db_client")
@Getter
@Setter
@NoArgsConstructor()
@RequiredArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    @Column(name = "first_name")
    private String firstName;

    @NonNull
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "sur_name")
    private String surname;

    @NonNull
    @Column(name = "country_of_residence")
    private String countryOfResidence;

    @Column(name = "date_accession")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime dateAccession;

    @NonNull
    @Column(name = "client_status")
    @Enumerated(EnumType.STRING)
    private ClientStatus clientStatus;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Contact contact;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private PassportData passportData;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile userProfile;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        if (contact != null)
            contact.setClient(this);
    }

    public void setPassportData(PassportData passportData) {
        this.passportData = passportData;
        if (passportData != null)
            passportData.setClient(this);
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
        if (userProfile != null)
            userProfile.setClient(this);
    }
}
