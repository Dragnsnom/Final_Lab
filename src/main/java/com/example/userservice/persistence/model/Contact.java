package com.example.userservice.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "db_contacts")
@Getter
@Setter
@NoArgsConstructor()
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "id_client", referencedColumnName = "id")
    private Client client;

    @Column(name = "sms_notification_enable")
    private Boolean smsNotificationEnable = true;

    @Column(name = "push_notification_enable")
    private Boolean pushNotificationEnable = true;

    @Column(name = "email_notification_enable")
    @ColumnDefault(value = "false")
    private Boolean emailNotificationEnable = false;

    @Column(name = "email")
    private String email;

    @NonNull
    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact contact)) return false;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
