package com.example.userservice.app.service;

import com.example.userservice.app.enums.ClientStatus;
import com.example.userservice.app.feign.CreditServiceClient;
import com.example.userservice.app.kafka.dto.RegisterUserDto;
import com.example.userservice.app.kafka.dto.enums.ClientFlow;
import com.example.userservice.app.service.impl.ClientServiceImpl;
import com.example.userservice.persistence.model.Client;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.model.PassportData;
import com.example.userservice.persistence.model.UserProfile;
import com.example.userservice.persistence.repository.ClientRepository;
import com.example.userservice.persistence.repository.ContactRepository;
import com.example.userservice.persistence.repository.UserProfileRepository;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.NotFoundException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.dto.requests.ClientDto;
import com.example.userservice.web.dto.requests.NonClientDto;
import com.example.userservice.web.dto.responses.RegistrationInfoDTO;
import com.example.userservice.web.dto.responses.UserInfoDto;
import com.example.userservice.web.mapper.ClientMapper;
import jakarta.validation.constraints.AssertFalse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@SpringBootTest
class ClientServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private CreditServiceClient creditServiceClient;

    @Mock
    private KafkaTemplate<String, RegisterUserDto> registerUserProducer;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Contact contact;

    private PassportData passportData;

    private UserProfile userProfile;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client("Владимир", "Маяковский", "Россия", ClientStatus.NOT_ACTIVE);
        passportData = new PassportData(client, "1234567890", LocalDate.of(2005, 10, 12), "Rostov",
                LocalDate.of(2025, 10, 12), LocalDate.of(1995, 10, 15));
        passportData.setId(UUID.randomUUID());
        contact = new Contact("79370458234");
        contact.setId(UUID.randomUUID());
        userProfile = new UserProfile(client, "ABC123abc/", "сколько будет 2+2 ?", "четыре");
        userProfile.setId(UUID.randomUUID());

        client.setPassportData(passportData);
        client.setContact(contact);
        client.setUserProfile(userProfile);
        client.setId(UUID.randomUUID());
    }

    @Test
    void checkRegistrationTest() {
        Client client = new Client(
                "Владимир", "Маяковский", "Россия", ClientStatus.NOT_ACTIVE
        );
        client.setId(UUID.randomUUID());

        Contact contact = new Contact("79370458234"
        );

        when(contactRepository.findContactByMobilePhone(anyString()))
                .thenReturn(Optional.of(contact));

        when(clientRepository.findClientByContact(any(Contact.class)))
                .thenReturn(Optional.of(client));

        Assertions.assertEquals(new RegistrationInfoDTO("79370458234", ClientStatus.NOT_ACTIVE.name()),
                clientService.findByMobilePhone("79370458234"));
    }

    @Test
    void checkRegistrationWithNonExistentPhoneNumberTest() {
        when(contactRepository.findContactByMobilePhone(anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertEquals(new RegistrationInfoDTO("79370458234", ClientStatus.NOT_REGISTERED.name()),
                clientService.findByMobilePhone("79370458234"));
    }

    @Test
    void checkRegistrationWithNonExistentClientTest() {
        Contact contact = new Contact();

        when(contactRepository.findContactByMobilePhone(anyString()))
                .thenReturn(Optional.of(contact));

        when(clientRepository.findClientByContact(any(Contact.class)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> clientService.findByMobilePhone("79370458234"));
    }

    @Test
    void nonClientRegistrationSuccess() {
        NonClientDto nonClientDto = new NonClientDto("Jon", "Snow", "RUS",
                "79370458234", "john.snow@gmail.com", "AB33455712",
                LocalDate.of(2019, 05, 20),
                "Russian federation", LocalDate.of(2029, 05, 20),
                LocalDate.of(1985, 05, 21),
                "ABC123abc/", "what do you know?", "nothing"
        );
        Client client = new Client();
        Contact contact = new Contact(nonClientDto.getMobilePhone());
        PassportData passportData = new PassportData(client, nonClientDto.getPassportNumber(),
                nonClientDto.getIssuanceDate(), nonClientDto.getPlaceOfIssue(),
                nonClientDto.getExpiryDate(), nonClientDto.getBirthDate());
        UserProfile userProfile = new UserProfile(client, nonClientDto.getPassword(),
                nonClientDto.getSecurityQuestion(), nonClientDto.getSecurityAnswer());
        client.setContact(contact);
        client.setPassportData(passportData);
        client.setUserProfile(userProfile);
        client.setClientStatus(ClientStatus.NOT_ACTIVE);

        when(clientMapper.toClient(nonClientDto)).thenReturn(client);

        clientService.nonClientRegistration(nonClientDto);

        verify(clientRepository).save(client);
        verify(registerUserProducer).send("register-user",
                new RegisterUserDto(client.getId(), client.getContact().getEmail(), ClientFlow.NEW));

    }

    @Test
    void clientRegistrationWithExistentClient() {
        ClientDto clientDto = new ClientDto(UUID.randomUUID(), "79370458234", "bob.millera@gmail.com",
                "ABC123abc/", "what is your pet name?", "barsik");
        Client client = new Client("Bob", "Miller", "USA", ClientStatus.NOT_ACTIVE);
        client.setId(clientDto.getId());
        Optional<Client> clientOptional = Optional.of(client);
        when(clientRepository.findById(clientDto.getId()))
                .thenReturn(clientOptional);

        Contact contact = new Contact(clientDto.getMobilePhone());
        contact.setEmail(clientDto.getEmail());
        when(clientMapper.toContact(clientDto))
                .thenReturn(contact);
        UserProfile userProfile = new UserProfile(client, clientDto.getPassword(),
                clientDto.getSecurityQuestion(), clientDto.getSecurityQuestion());
        when(clientMapper.toUserProfile(clientDto))
                .thenReturn(userProfile);

        when(creditServiceClient.checkClientCredits(clientDto.getId()))
                .thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));

        clientService.clientRegistration(clientDto);
        verify(clientRepository).save(client);
    }

    @Test
    void clientRegistrationWithNonExistentClient() {
        ClientDto clientDto = new ClientDto(UUID.randomUUID(), "79370458234", "bob.millera@gmail.com",
                "ABC123abc/", "what is your pet name?", "barsik");

        when(clientRepository.findById(clientDto.getId()))
                .thenThrow(UnprocessableEntityException.class);

        Assertions.assertThrows(UnprocessableEntityException.class, () ->
                clientService.clientRegistration(clientDto));
    }

    @Test
    void getUserInfoDto_whenNormal_thenReturnUserInfoDto() {
        when(clientRepository.findById(client.getId()))
                .thenReturn(Optional.of(client));
        UserInfoDto expected = new UserInfoDto(client.getFirstName(), client.getLastName(), client.getSurname(),
                client.getContact().getEmail(), client.getContact().getMobilePhone(),
                client.getPassportData().getIdentificationPassportNumber());
        UserInfoDto userInfoDto = clientService.getUserInfoById(client.getId());

        assertAll(
                "UserInfoDto and Client-entity",
                () -> assertEquals(userInfoDto.getFirstName(), expected.getFirstName()),
                () -> assertEquals(userInfoDto.getLastName(), expected.getLastName()),
                () -> assertEquals(userInfoDto.getSurname(), expected.getSurname()),
                () -> assertEquals(userInfoDto.getPassportNumber(), expected.getPassportNumber()),
                () -> assertEquals(userInfoDto.getMobilePhone(), expected.getMobilePhone()),
                () -> assertEquals(userInfoDto.getEmail(), expected.getEmail())
        );
    }

    @Test
    void getUserInfoDto_whenNotFound_throwNotFoundException() {
        doThrow(new NotFoundException("Client Not Found"))
                .when(clientRepository).findById(client.getId());

        assertThrows(NotFoundException.class, () -> clientService.getUserInfoById(client.getId()));
    }

    @Test
    void changeClientStatus_whenOk_thenChangeInRepository() {
        UUID id = UUID.randomUUID();
        ClientStatus clientStatusForChanging = ClientStatus.NOT_ACTIVE;

        clientService.changeClientStatus(id, clientStatusForChanging);
        verify(clientRepository).changeClientStatusById(id, clientStatusForChanging);
    }

    @Test
    void cancelClientRegistration_whenClientIdExist_thenChangeInRepository() {
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        clientService.cancelClientRegistration(client.getId());

        verify(clientRepository).changeClientStatusById(client.getId(), ClientStatus.NOT_CLIENT);
        assertEquals(null, client.getContact());
        assertEquals(null, client.getUserProfile());
    }

    @Test
    void cancelClientRegistration_whenClientIdNotExist_thenDoNothing() {
        when(clientRepository.findById(client.getId())).thenReturn(Optional.empty());
        clientService.cancelClientRegistration(client.getId());

        verify(clientRepository, never()).changeClientStatusById(any(UUID.class), any(ClientStatus.class));
        assertNotEquals(null, client.getContact());
        assertNotEquals(null, client.getUserProfile());

    }

}