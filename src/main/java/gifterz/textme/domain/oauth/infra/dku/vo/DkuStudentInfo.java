package gifterz.textme.domain.oauth.infra.dku.vo;

import gifterz.textme.domain.user.entity.Major;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Nullable
public class DkuStudentInfo {
    private final Long userId;
    private final String studentId;
    private final String username;
    private final String nickname;
    private final String major;
    private final String department;
    private final String age;
    private final String gender;
    private final String yearOfAdmission;
    private final String phoneNumber;
    private final String profileImage;
    private final Long writePostCount;
    private final Long commentedPostCount;
    private final Long likedPostCount;
    private final Long petitionCount;
    private final Long agreedPetitionCount;
    private final boolean isDkuChecked;
    private final boolean admin;

    public String getEmail() {
        return this.studentId + "@gmail.com";
    }

    public Major toMajor() {
        return Major.of(this.department, this.major);
    }
}
