package gifterz.textme.domain.letter.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Target {
    private String contents;
    private String imageUrl;

    public static Target of(String contents, String imageUrl) {
        return new Target(contents, imageUrl);
    }
}
