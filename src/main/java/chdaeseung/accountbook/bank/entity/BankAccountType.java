package chdaeseung.accountbook.bank.entity;

public enum BankAccountType {
    CHECKING("입출금"),
    SAVING("적금"),
    DEPOSIT("예금"),
    CASH("현금");

    private final String description;

    BankAccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
