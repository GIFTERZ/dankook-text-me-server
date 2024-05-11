package gifterz.textme.domain.letter.service;

import gifterz.textme.domain.entity.StatusType;
import gifterz.textme.domain.letter.dto.request.SenderInfo;
import gifterz.textme.domain.letter.dto.request.Target;
import gifterz.textme.domain.letter.dto.response.AllEventLetterResponse;
import gifterz.textme.domain.letter.entity.EventLetter;
import gifterz.textme.domain.letter.repository.EventLetterRepository;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.exception.UserNotFoundException;
import gifterz.textme.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventLetterService {
    private final EventLetterRepository eventLetterRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendLetter(Long senderId, SenderInfo senderInfo, Target letterInfo) {
        User user = userRepository.findById(senderId).orElseThrow(UserNotFoundException::new);
        EventLetter eventLetter = EventLetter.of(user, senderInfo.getSenderName(), letterInfo.getContents(),
                letterInfo.getImageUrl(), senderInfo.getContactInfo());
        eventLetterRepository.save(eventLetter);
    }

    public List<AllEventLetterResponse> getLettersByGender(String gender) {
        if (gender.equals("men")) {
            gender = "남자";
        } else if (gender.equals("women")) {
            gender = "여자";
        }

        List<EventLetter> eventLettersByGender = eventLetterRepository.findAllByUserGenderAndStatus(gender, StatusType.ACTIVATE.getStatus());
        return eventLettersByGender.stream()
                .map(eventLetter -> AllEventLetterResponse.builder()
                        .id(eventLetter.getId())
                        .imageUrl(eventLetter.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
