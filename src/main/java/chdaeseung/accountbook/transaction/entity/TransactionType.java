package chdaeseung.accountbook.transaction.entity;

public enum TransactionType {
    INCOME("수입"), EXPENSE("지출");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
