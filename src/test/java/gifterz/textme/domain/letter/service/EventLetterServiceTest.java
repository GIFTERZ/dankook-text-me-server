package gifterz.textme.domain.letter.service;

import gifterz.textme.domain.entity.StatusType;
import gifterz.textme.domain.letter.dto.request.SenderInfo;
import gifterz.textme.domain.letter.dto.request.Target;
import gifterz.textme.domain.letter.dto.response.EventLetterResponse;
import gifterz.textme.domain.letter.entity.EventLetter;
import gifterz.textme.domain.letter.repository.EventLetterLogRepository;
import gifterz.textme.domain.letter.repository.EventLetterRepository;
import gifterz.textme.domain.oauth.entity.AuthType;
import gifterz.textme.domain.user.entity.Major;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.repository.UserRepository;
import gifterz.textme.error.exception.ApplicationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static gifterz.textme.domain.letter.entity.EventLetter.MAX_VIEW_COUNT;
import static gifterz.textme.domain.letter.service.EventLetterService.viewMap;
import static org.assertj.core.api.Assertions.assertThat;
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

    @AfterEach()
    void clearViewMap() {
        viewMap.clear();
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

    @Test
    void getEventLetter() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventLetterRepository.findByUser(user)).thenReturn(Optional.of(eventLetter));
        when(eventLetterRepository.findByIdWithPessimistic(1L, StatusType.ACTIVATE.getStatus()))
                .thenReturn(Optional.of(eventLetter));

        // When
        WhoseEventLetterResponse response = eventLetterService.findLetter(1L, 1L);

        // Then
        assertAll(
                () -> assertThat(response.id()).isEqualTo(eventLetter.getId()),
                () -> assertThat(response.senderName()).isEqualTo(eventLetter.getSenderName()),
                () -> assertThat(response.contents()).isEqualTo(eventLetter.getContents()),
                () -> assertThat(response.imageUrl()).isEqualTo(eventLetter.getImageUrl()),
                () -> assertThat(response.contactInfo()).isEqualTo(eventLetter.getContactInfo())
        );
    }

    @Test
    void getEventLetterWithUserViewCountOver3() {
        // Given
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(eventLetterLogRepository.countByUser(any())).thenReturn(3L);

        // When, Then
        assertThrows(ApplicationException.class,
                () -> eventLetterService.findLetter(user.getId(), eventLetter.getId()),
                "이미 " + MAX_VIEW_COUNT + "번 조회한 사용자입니다."
        );
    }

    @Test
    void getEventLetterWithLetterViewCountOver3() {
        // Given
        EventLetter eventLetter = EventLetter.of(user, "senderName", "contents", "imageUrl", "contactInfo");
        eventLetter.increaseViewCount();
        eventLetter.increaseViewCount();
        eventLetter.increaseViewCount();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(eventLetterLogRepository.countByUser(any())).thenReturn(0L);
        when(eventLetterRepository.findByIdWithPessimistic(any(), any())).thenReturn(Optional.of(eventLetter));

        // When, Then
        assertThrows(ApplicationException.class,
                () -> eventLetterService.findLetter(user.getId(), eventLetter.getId()),
                "이미 " + MAX_VIEW_COUNT + "번 조회된 편지입니다.");
    }

    @Test
    void getEventLetterSimultaneously() throws InterruptedException {
        // Given
        int threadCount = 4000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(eventLetterRepository.findByIdWithPessimistic(any(), any())).thenReturn(Optional.of(eventLetter));
        // when
        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    eventLetterService.findLetter(user.getId(), eventLetter.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println("실행 시간: " + (end - start) + "ms");
        // then
        assertThat(eventLetter.getViewCount()).isEqualTo(4000);
    }

    @Test
    void reportEventLetter() {
        // Given
        when(eventLetterRepository.findById(any())).thenReturn(Optional.of(eventLetter));

        // When
        eventLetterService.reportLetter(1L);

        // Then
        assertThat(eventLetter.getStatus()).isEqualTo(StatusType.PENDING.getStatus());
    }

    @Test
    void findEventLetterThatIsMine() {
        // Given
        when(eventLetterRepository.findByIdWithPessimistic(any(), any())).thenReturn(Optional.of(eventLetter));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        // When
        EventLetterResponse response = eventLetterService.findLetter(1L, 1L);

        // Then
        assertThat(response).isNotNull();
    }
}
