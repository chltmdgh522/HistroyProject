package history.history.board.domain.board;

public enum BoardType {

    Donate("기부 게시판");

    private final String description;

    BoardType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
