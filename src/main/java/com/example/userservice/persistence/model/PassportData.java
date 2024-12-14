package com.example.userservice.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "db_passport_data")
@Getter
@Setter
@NoArgsConstructor()
@RequiredArgsConstructor
public class PassportData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "id_client", referencedColumnName = "id")
    @NonNull
    private Client client;

    @NonNull
    @Column(name = "identification_passport_number")
    private String identificationPassportNumber;

    @NonNull
    @Column(name = "issuance_date")
    @Temporal(TemporalType.DATE)
    private LocalDate issuanceDate;

    @NonNull
    @Column(name = "place_of_issue")
    private String placeOfIssue;

    @NonNull
    @Column(name = "expiry_date")
    @Temporal(TemporalType.DATE)
    private LocalDate expiryDate;

    @Column(name = "nationality")
    private String nationality;

    @NonNull
    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PassportData that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
