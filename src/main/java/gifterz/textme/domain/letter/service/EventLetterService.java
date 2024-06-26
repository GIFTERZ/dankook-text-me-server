package gifterz.textme.domain.letter.service;

import gifterz.textme.domain.entity.StatusType;
import gifterz.textme.domain.letter.dto.request.SenderInfo;
import gifterz.textme.domain.letter.dto.request.Target;
import gifterz.textme.domain.letter.dto.response.AdminEventLetterResponse;
import gifterz.textme.domain.letter.dto.response.AllEventLetterResponse;
import gifterz.textme.domain.letter.dto.response.EventLetterResponse;
import gifterz.textme.domain.letter.dto.response.WhoseEventLetterResponse;
import gifterz.textme.domain.letter.entity.EventLetter;
import gifterz.textme.domain.letter.entity.EventLetterLog;
import gifterz.textme.domain.letter.exception.*;
import gifterz.textme.domain.letter.repository.EventLetterLogRepository;
import gifterz.textme.domain.letter.repository.EventLetterRepository;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.exception.UserNotFoundException;
import gifterz.textme.domain.user.repository.UserRepository;
import org.springframework.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static gifterz.textme.domain.entity.StatusType.ACTIVATE;
import static gifterz.textme.domain.entity.StatusType.PENDING;
import static gifterz.textme.domain.letter.entity.EventLetter.MAX_VIEW_COUNT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventLetterService {
    private final EventLetterRepository eventLetterRepository;
    private final EventLetterLogRepository eventLetterLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendLetter(Long senderId, SenderInfo senderInfo, Target letterInfo) {
        User user = userRepository.findById(senderId).orElseThrow(UserNotFoundException::new);
        eventLetterRepository.findByUser(user).ifPresent(eventLetter -> {
            throw new AlreadyPostedUserException();
        });
        EventLetter eventLetter = EventLetter.of(user, senderInfo.getSenderName(), letterInfo.getContents(),
                letterInfo.getImageUrl(), senderInfo.getContactInfo());
        eventLetterRepository.save(eventLetter);
    }

    public List<AllEventLetterResponse> getLettersByFiltering(Long userId, String gender, Boolean hasContact) {
        List<EventLetter> eventLetters = findEventLettersByGenderAndContact(gender, hasContact);
        List<EventLetterLog> viewedLogs = eventLetterLogRepository.findAllByUserId(userId);
        if (checkViewEvent(viewedLogs)) {
            return getAllEventLetterResponses(userId, eventLetters, filterIsNotEventAndIsNotMine(userId));
        }
        return getAllEventLetterResponses(userId, eventLetters, filterIsEventOrIsNotMine(userId));
    }

    private Predicate<EventLetter> filterIsNotEventAndIsNotMine(Long userId) {
        return eventLetter -> !eventLetter.getIsEvent() && !eventLetter.isUserLetter(userId);
    }

    private Predicate<EventLetter> filterIsEventOrIsNotMine(Long userId) {
        return eventLetter -> eventLetter.getIsEvent() || !eventLetter.isUserLetter(userId);
    }

    private List<AllEventLetterResponse> getAllEventLetterResponses(Long userId, List<EventLetter> eventLetters,
                                                                    Predicate<EventLetter> filter) {
        return eventLetters.stream()
                .filter(filter)
                .map(eventLetter -> AllEventLetterResponse.builder()
                        .id(eventLetter.getId())
                        .imageUrl(eventLetter.getImageUrl())
                        .isMine(eventLetter.isUserLetter(userId))
                        .build())
                .toList();
    }

    private boolean checkViewEvent(List<EventLetterLog> viewedLogs) {
        boolean isViewedEventLetter = false;
        for (EventLetterLog eventLetterLog : viewedLogs) {
            EventLetter viewedEventLetter = eventLetterLog.getEventLetter();
            if (viewedEventLetter.getIsEvent()) {
                isViewedEventLetter = true;
                break;
            }
        }
        return isViewedEventLetter;
    }

    private List<EventLetter> findEventLettersByGenderAndContact(String gender, Boolean hasContact) {
        if (StringUtils.hasText(gender)) {
            gender = convertGender(gender);
            if (hasContact == null || !hasContact) {
                return eventLetterRepository.findAllByUserGenderAndStatus(gender, ACTIVATE.getStatus());
            }
            return eventLetterRepository.findAllByUserGenderAndStatusAndContactInfoNotNull(gender, ACTIVATE.getStatus());
        }

        if (hasContact == null || !hasContact) {
            return eventLetterRepository.findAllByStatus(ACTIVATE.getStatus());
        }
        return eventLetterRepository.findAllByStatusAndContactInfoNotNull(ACTIVATE.getStatus());
    }

    private String convertGender(String gender) {
        if (gender.equals("men")) {
            gender = "남자";
        } else if (gender.equals("women")) {
            gender = "여자";
        }
        return gender;
    }

    @Transactional
    public WhoseEventLetterResponse findLetter(Long userId, Long letterId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        EventLetter myEventLetter = eventLetterRepository.findByUser(user).orElseThrow(NotPostedUserException::new);

        List<EventLetterLog> viewedLogs = eventLetterLogRepository.findAllByUserId(userId);
        long userViewCount = viewCountWithoutEvent(viewedLogs);
        checkUserViewCount(userViewCount);

        EventLetter eventLetter = eventLetterRepository
                .findByIdWithPessimistic(letterId, ACTIVATE.getStatus()).orElseThrow(LetterNotFoundException::new);
        checkLetterViewCount(eventLetter.getViewCount());

        if (myEventLetter.equals(eventLetter)) {
            return WhoseEventLetterResponse.of(eventLetter, true);
        }

        checkIsViewedEventLetter(viewedLogs, eventLetter);

        eventLetter.increaseViewCount();

        EventLetterLog eventLetterLog = EventLetterLog.of(user, eventLetter);
        eventLetterLogRepository.save(eventLetterLog);

        return WhoseEventLetterResponse.of(eventLetter, false);
    }

    private long viewCountWithoutEvent(List<EventLetterLog> viewedLogs) {
        return viewedLogs.stream()
                .filter(eventLetterLog -> !eventLetterLog.getEventLetter().getIsEvent())
                .count();
    }

    private void checkIsViewedEventLetter(List<EventLetterLog> viewedLogs, EventLetter eventLetter) {
        viewedLogs.forEach(eventLetterLog -> {
            EventLetter viewedEventLetter = eventLetterLog.getEventLetter();
            Long viewedEventLetterId = viewedEventLetter.getId();
            if (Objects.equals(eventLetter.getId(), viewedEventLetterId)) {
                throw new AlreadyViewedUserException();
            }
        });
    }

    private void checkUserViewCount(long viewCount) {
        if (viewCount >= MAX_VIEW_COUNT) {
            throw new ExceedUserViewCountException();
        }
    }

    private void checkLetterViewCount(long viewCount) {
        if (viewCount >= MAX_VIEW_COUNT) {
            throw new ExceedLetterViewCountException();
        }
    }

    @Transactional
    public void reportLetter(Long eventLetterId) {
        EventLetter eventLetter = eventLetterRepository.findById(eventLetterId).orElseThrow(LetterNotFoundException::new);

        eventLetter.changeStatus(PENDING.getStatus());
    }

    public List<AdminEventLetterResponse> findAllLettersByStatus(String status) {
        List<EventLetter> eventLetters = findEventLettersByStatus(status);

        return eventLetters.stream()
                .map(eventLetter -> AdminEventLetterResponse.builder()
                        .id(eventLetter.getId())
                        .senderName(eventLetter.getSenderName())
                        .contents(eventLetter.getContents())
                        .imageUrl(eventLetter.getImageUrl())
                        .contactInfo(eventLetter.getContactInfo())
                        .viewCount(eventLetter.getViewCount())
                        .status(eventLetter.getStatus())
                        .build())
                .toList();
    }

    private List<EventLetter> findEventLettersByStatus(String status) {
        if (StringUtils.hasText(status)) {
            status = convertStatus(status);
            return eventLetterRepository.findAllByStatus(status);
        }

        return eventLetterRepository.findAll();
    }

    private String convertStatus(String status) {
        return StatusType.fromStatus(status).getStatus();
    }

    @Transactional
    public void changeLetterStatus(Long letterId, String status) {
        EventLetter eventLetter = eventLetterRepository.findById(letterId).orElseThrow(LetterNotFoundException::new);

        status = convertStatus(status);
        eventLetter.changeStatus(status);
    }

    public List<EventLetterResponse> findLettersByUser(Long userId) {
        List<EventLetterLog> eventLetterLogs = eventLetterLogRepository.findAllByUserId(userId);

        return eventLetterLogs.stream()
                .map(eventLetterLog -> EventLetterResponse.from(eventLetterLog.getEventLetter()))
                .toList();
    }

    public List<EventLetterResponse> findLettersByContacts(String gender) {
        if (StringUtils.hasText(gender)) {
            gender = convertGender(gender);
            return eventLetterRepository.findAllByContactInfoContainingAndGender(gender, ACTIVATE.getStatus()).stream()
                    .map(EventLetterResponse::from)
                    .toList();
        }

        return eventLetterRepository.findByContactInfoContaining(ACTIVATE.getStatus()).stream()
                .map(EventLetterResponse::from)
                .toList();
    }
}
