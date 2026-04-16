package chdaeseung.accountbook.bank.dto;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.bank.entity.BankAccountType;
import chdaeseung.accountbook.dashboard.dto.AssetTrendPointDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BankAccountDetailResponseDto {
    private long id;

    private String bankName;

    private String accountName;

    private Long balance;

    private BankAccountType type;

    private boolean negativeBalanceAllowed;

    private List<AssetTrendPointDto> monthlyExpenses;

    private List<AssetTrendPointDto> expenseTopCategories;

    public static BankAccountDetailResponseDto of(BankAccount bankAccount, List<AssetTrendPointDto> monthlyExpenses, List<AssetTrendPointDto> expenseTopCategories) {
        return BankAccountDetailResponseDto.builder()
                .id(bankAccount.getId())
                .bankName(bankAccount.getBankName())
                .accountName(bankAccount.getAccountName())
                .balance(bankAccount.getBalance())
                .type(bankAccount.getType())
                .negativeBalanceAllowed(bankAccount.isNegativeBalanceAllowed())
                .monthlyExpenses(monthlyExpenses)
                .expenseTopCategories(expenseTopCategories)
                .build();
    }
}
