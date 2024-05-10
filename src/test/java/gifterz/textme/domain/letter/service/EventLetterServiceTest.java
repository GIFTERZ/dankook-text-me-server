package gifterz.textme.domain.letter.service;

import gifterz.textme.domain.letter.dto.request.SenderInfo;
import gifterz.textme.domain.letter.dto.request.Target;
import gifterz.textme.domain.letter.entity.EventLetter;
import gifterz.textme.domain.letter.repository.EventLetterLogRepository;
import gifterz.textme.domain.letter.repository.EventLetterRepository;
import gifterz.textme.domain.oauth.entity.AuthType;
import gifterz.textme.domain.user.entity.Major;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.yml")
class EventLetterServiceTest {

    @InjectMocks
    private EventLetterService eventLetterService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventLetterRepository eventLetterRepository;
    @Mock
    private EventLetterLogRepository eventLetterLogRepository;
    private User user;
    private EventLetter eventLetter;

    @BeforeEach
    void setUp() {
        Major major = Major.of("department", "major");
        user = User.of("email", "name", AuthType.PASSWORD, major, "남자");
        eventLetter = EventLetter.of(user, "senderName", "contents", "imageUrl", "contactInfo");
    }

    @Test
    void sendEventLetter() {
        // Given
        SenderInfo senderInfo = SenderInfo.builder().senderName("name").contactInfo("contact").build();
        Target letterInfo = Target.of("contents", "imageUrl");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        EventLetter expectedEventLetter = EventLetter.of(user, senderInfo.getSenderName(), letterInfo.getContents(),
                letterInfo.getImageUrl(), senderInfo.getContactInfo());

        // When
        eventLetterService.sendLetter(user.getId(), senderInfo, letterInfo);

        // Then
        ArgumentCaptor<EventLetter> eventLetterArgumentCaptor = ArgumentCaptor.forClass(EventLetter.class);
        verify(eventLetterRepository, times(1)).save(eventLetterArgumentCaptor.capture());
        EventLetter savedEventLetter = eventLetterArgumentCaptor.getValue();
        assertEquals(expectedEventLetter, savedEventLetter);
    }

}