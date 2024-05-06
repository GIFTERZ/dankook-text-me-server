package gifterz.textme.domain.letter.service;

import gifterz.textme.domain.letter.dto.request.SenderInfo;
import gifterz.textme.domain.letter.dto.request.Target;
import gifterz.textme.domain.letter.entity.EventLetter;
import gifterz.textme.domain.letter.repository.EventLetterRepository;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.exception.UserNotFoundException;
import gifterz.textme.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
