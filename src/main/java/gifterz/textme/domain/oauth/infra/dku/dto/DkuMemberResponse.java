package gifterz.textme.domain.oauth.infra.dku.dto;

public record DkuMemberResponse(
        Long userId,
        String studentId,
        String name,
        MajorInfo major
) {
    public String studentIdToEmail() {
        return this.studentId + "@gmail.com";
    }
}
