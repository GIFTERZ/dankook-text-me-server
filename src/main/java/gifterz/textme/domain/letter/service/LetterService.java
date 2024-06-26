package gifterz.textme.domain.letter.service;

import gifterz.textme.common.firebase.FCMService;
import gifterz.textme.domain.letter.dto.request.LetterRequest;
import gifterz.textme.domain.letter.dto.request.ReceiverInfo;
import gifterz.textme.domain.letter.dto.request.SenderInfo;
import gifterz.textme.domain.letter.dto.request.Target;
import gifterz.textme.domain.letter.dto.response.AllLetterResponse;
import gifterz.textme.domain.letter.dto.response.LetterResponse;
import gifterz.textme.domain.letter.dto.response.SlowLetterWithAddressResponse;
import gifterz.textme.domain.letter.dto.response.SlowLetterWithEmailResponse;
import gifterz.textme.domain.letter.entity.Address;
import gifterz.textme.domain.letter.entity.Letter;
import gifterz.textme.domain.letter.entity.SlowLetter;
import gifterz.textme.domain.letter.exception.LetterNotFoundException;
import gifterz.textme.domain.letter.repository.LetterRepository;
import gifterz.textme.domain.letter.repository.SlowLetterRepository;
import gifterz.textme.global.security.service.AesUtils;
import gifterz.textme.domain.user.entity.Major;
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
public class LetterService {

    private final UserRepository userRepository;
    private final LetterRepository letterRepository;
    private final SlowLetterRepository slowLetterRepository;
    private final FCMService fcmService;
    private final AesUtils aesUtils;

    @Transactional
    public LetterResponse makeLetter(LetterRequest request) {
        String userId = aesUtils.decrypt(request.getReceiverId());
        Long id = Long.parseLong(userId);
        User receiver = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        Letter letter = Letter.of(receiver, request.getSenderName(), request.getContents(), request.getImageUrl());
        letterRepository.save(letter);
//        fcmService.sendLetterReceivedNotification(receiver.getEmail());
        return new LetterResponse(letter.getId(), receiver.getName(),
                request.getSenderName(), request.getContents(), request.getImageUrl());
    }

    public List<AllLetterResponse> findLettersByUserId(String encryptedId) {
        String userId = aesUtils.decrypt(encryptedId);
        long id = Long.parseLong(userId);
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        List<Letter> letterList = letterRepository.findAllByUserId(user.getId());
        return letterList.stream()
                .map(letter -> AllLetterResponse.builder()
                        .id(letter.getId())
                        .receiverName(user.getName())
                        .imageUrl(letter.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    public LetterResponse findLetter(Long id) {
        Letter letter = letterRepository.findById(id).orElseThrow(LetterNotFoundException::new);
        User user = letter.getUser();
        Major major = user.getMajor();

        if (major == null) {
            return new LetterResponse(letter.getId(), user.getName(), letter.getSenderName(),
                    letter.getContents(), letter.getImageUrl());
        }
        String department = major.getDepartment();
        return new LetterResponse(letter.getId(), user.getName(), department,
                letter.getContents(), letter.getImageUrl());
    }

    @Transactional
    public SlowLetterWithEmailResponse sendSlowLetterWithEmail(SenderInfo senderInfo, Target letterInfo) {
        String email = senderInfo.getEmail();
        String senderName = senderInfo.getSenderName();
        String imageUrl = letterInfo.getImageUrl();
        String contents = letterInfo.getContents();
        SlowLetter slowLetter = SlowLetter.of(email, senderName, imageUrl, contents);
        slowLetterRepository.save(slowLetter);
        return new SlowLetterWithEmailResponse(senderInfo, contents);
    }

    @Transactional
    public SlowLetterWithAddressResponse sendSlowLetterWithAddress(
            SenderInfo senderInfo, ReceiverInfo receiverInfo, String contents) {
        String senderName = senderInfo.getSenderName();
        String phoneNumber = receiverInfo.getPhoneNumber();
        Address address = receiverInfo.getAddress();
        String receiverName = receiverInfo.getReceiverName();
        SlowLetter slowLetter = SlowLetter.of(address, contents, senderName, receiverName, phoneNumber);
        slowLetterRepository.save(slowLetter);
        return new SlowLetterWithAddressResponse(senderInfo, receiverInfo, contents);
    }
}
