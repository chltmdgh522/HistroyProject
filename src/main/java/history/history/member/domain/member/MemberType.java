package history.history.member.domain.member;

public enum MemberType {
    MAN("남자"), WOMAN("여자");

    private final String description;

    MemberType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}


