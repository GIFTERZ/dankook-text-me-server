package gifterz.textme.domain.oauth.infra.dku.dto;

import gifterz.textme.domain.oauth.infra.dku.vo.DkuStudentInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Nullable
@Builder
public record DkuMemberResponse(
        @NotBlank
        Long userId,
        @NotBlank
        String studentId,
        @NotBlank
        String name,
        String nickname,
        @NotBlank
        String major,
        @NotBlank
        String department,
        String age,
        String gender,
        String yearOfAdmission,
        String phoneNumber,
        String profileImage,
        Long writePostCount,
        Long commentedPostCount,
        Long likedPostCount,
        Long petitionCount,
        Long agreedPetitionCount,
        boolean isDkuChecked,
        boolean admin
) {

    public DkuStudentInfo toDkuStudentInfo() {
        return DkuStudentInfo.builder()
                .userId(this.userId)
                .studentId(this.studentId)
                .username(this.name)
                .nickname(this.nickname)
                .major(this.major)
                .department(this.department)
                .age(this.age)
                .gender(this.gender)
                .yearOfAdmission(this.yearOfAdmission)
                .phoneNumber(this.phoneNumber)
                .profileImage(this.profileImage)
                .writePostCount(this.writePostCount)
                .commentedPostCount(this.commentedPostCount)
                .likedPostCount(this.likedPostCount)
                .petitionCount(this.petitionCount)
                .agreedPetitionCount(this.agreedPetitionCount)
                .isDkuChecked(this.isDkuChecked)
                .admin(this.admin)
                .build();

    }
}
