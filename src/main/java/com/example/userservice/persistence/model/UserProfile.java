package com.example.userservice.persistence.model;

import com.example.userservice.app.enums.AuthorizationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "db_user_profile")
@Getter
@Setter
@NoArgsConstructor()
@RequiredArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "id_client", referencedColumnName = "id")
    @NonNull
    private Client client;

    @NonNull
    @Column(name = "password_encoded")
    private String passwordEncoded;

    @NonNull
    @Column(name = "security_question")
    private String securityQuestion;

    @NonNull
    @Column(name = "security_answer")
    private String securityAnswer;

    @Column(name = "date_app_registration")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime dateAppRegistration;

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "user_authorization")
    @Enumerated(EnumType.STRING)
    private AuthorizationType authorization;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfile that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

