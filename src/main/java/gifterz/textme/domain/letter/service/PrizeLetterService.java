package gifterz.textme.domain.letter.service;

import gifterz.textme.domain.letter.dto.response.AllPrizeLettersResponse;
import gifterz.textme.domain.letter.dto.response.PrizeLetterResponse;
import gifterz.textme.domain.letter.entity.PrizeLetter;
import gifterz.textme.domain.letter.entity.PrizeLetterVO;
import gifterz.textme.domain.letter.exception.PrizeLetterNotFoundException;
import gifterz.textme.domain.letter.repository.PrizeLetterRepository;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.exception.UserNotFoundException;
import gifterz.textme.domain.user.repository.UserRepository;
import gifterz.textme.s3Proxy.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrizeLetterService {
    private final S3Service s3Service;
    private final PrizeLetterRepository prizeLetterRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendLetter(Long senderId, PrizeLetterVO prizeLetterVO) {
        User user = userRepository.findById(senderId).orElseThrow(UserNotFoundException::new);

        String webInfoImageUrl = s3Service.upload(prizeLetterVO.getWebInfoImage());
        String paymentImageUrl = getPaymentImageUrl(prizeLetterVO.getPaymentImage());

        PrizeLetter prizeLetter = PrizeLetter.of(user, prizeLetterVO.getContents(), webInfoImageUrl, paymentImageUrl,
                prizeLetterVO.getCardImageUrl(), prizeLetterVO.getCategory(), prizeLetterVO.getPhoneNumber());

        prizeLetterRepository.save(prizeLetter);
    }

    private String getPaymentImageUrl(MultipartFile paymentImage) {
        String paymentImageUrl = null;
        if (paymentImage == null || paymentImage.getSize() > 0) {
            paymentImageUrl = s3Service.upload(paymentImage);
        }
        return paymentImageUrl;
    }

    public List<AllPrizeLettersResponse> getPrizeLetters() {
        List<PrizeLetter> prizeLetters = prizeLetterRepository.findAll();
        return prizeLetters.stream()
                .map(prizeLetter -> AllPrizeLettersResponse.builder()
                        .id(prizeLetter.getId())
                        .senderName(prizeLetter.getUser().getName())
                        .cardImageUrl(prizeLetter.getCardImageUrl())
                        .build())
                .toList();
    }

    public PrizeLetterResponse getPrizeLetter(Long id) {
        PrizeLetter prizeLetter = prizeLetterRepository.findById(id).orElseThrow(PrizeLetterNotFoundException::new);
        return PrizeLetterResponse.builder()
                .id(prizeLetter.getId())
                .senderName(prizeLetter.getUser().getName())
                .contents(prizeLetter.getContents())
                .webInfoImageUrl(prizeLetter.getWebInfoImageUrl())
                .paymentImageUrl(prizeLetter.getPaymentImageUrl())
                .cardImageUrl(prizeLetter.getCardImageUrl())
                .category(prizeLetter.getCategory().name())
                .build();
    }
}
