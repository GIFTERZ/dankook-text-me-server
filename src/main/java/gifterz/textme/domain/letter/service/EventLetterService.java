package gifterz.textme.domain.letter.service;

import gifterz.textme.domain.entity.StatusType;
import gifterz.textme.domain.letter.dto.request.SenderInfo;
import gifterz.textme.domain.letter.dto.request.Target;
import gifterz.textme.domain.letter.dto.response.AllEventLetterResponse;
import gifterz.textme.domain.letter.dto.response.EventLetterResponse;
import gifterz.textme.domain.letter.entity.EventLetter;
import gifterz.textme.domain.letter.entity.EventLetterLog;
import gifterz.textme.domain.letter.exception.AlreadyViewedUserException;
import gifterz.textme.domain.letter.exception.ExceedLetterViewCountException;
import gifterz.textme.domain.letter.exception.ExceedUserViewCountException;
import gifterz.textme.domain.letter.exception.LetterNotFoundException;
import gifterz.textme.domain.letter.repository.EventLetterLogRepository;
import gifterz.textme.domain.letter.repository.EventLetterRepository;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.exception.UserNotFoundException;
import gifterz.textme.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static gifterz.textme.domain.letter.entity.EventLetter.MAX_VIEW_COUNT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EventLetterService {
    private final EventLetterRepository eventLetterRepository;
    private final EventLetterLogRepository eventLetterLogRepository;
    private final UserRepository userRepository;
    public static ConcurrentHashMap<Long, HashSet<Long>> viewMap = new ConcurrentHashMap<>();

    @Transactional
    public void sendLetter(Long senderId, SenderInfo senderInfo, Target letterInfo) {
        User user = userRepository.findById(senderId).orElseThrow(UserNotFoundException::new);
        EventLetter eventLetter = EventLetter.of(user, senderInfo.getSenderName(), letterInfo.getContents(),
                letterInfo.getImageUrl(), senderInfo.getContactInfo());
        eventLetterRepository.save(eventLetter);
    }

    public List<AllEventLetterResponse> getLettersByGender(String gender) {
        gender = convertGender(gender);

        List<EventLetter> eventLettersByGender = eventLetterRepository.findAllByUserGenderAndStatus(gender, StatusType.ACTIVATE.getStatus());
        return eventLettersByGender.stream()
                .map(eventLetter -> AllEventLetterResponse.builder()
                        .id(eventLetter.getId())
                        .imageUrl(eventLetter.getImageUrl())
                        .build())
                .toList();
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
    public synchronized EventLetterResponse findLetter(Long userId, Long letterId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        long userViewCount = eventLetterLogRepository.countByUser(user);
        checkUserViewCount(userViewCount);
        EventLetter eventLetter = getEventLetterByIdWithOptimistic(letterId).orElseThrow(LetterNotFoundException::new);
        checkLetterViewCount(eventLetter.getViewCount());
        eventLetter.increaseViewCount();
        checkAlreadyViewedUser(userId, letterId);
        HashSet<Long> userViewedSet = viewMap.getOrDefault(1L, new HashSet<>());
        userViewedSet.add(1L);
        viewMap.put(1L, userViewedSet);
        EventLetterLog eventLetterLog = EventLetterLog.of(user, eventLetter);
        eventLetterLogRepository.save(eventLetterLog);
        return EventLetterResponse.of(eventLetter);
    }

    private void checkAlreadyViewedUser(Long userId, Long letterId) {
        if (viewMap.getOrDefault(letterId, new HashSet<>()).contains(userId)) {
            throw new AlreadyViewedUserException();
        }
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
}
