package chdaeseung.accountbook.bank.dto;

import chdaeseung.accountbook.dashboard.dto.AssetTrendPointDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AssetDashboardResponseDto {
    private Long totalAsset;
    private Long monthAssetIncrease;
    private Long monthAssetDecrease;

    private int negativeAllowedCount;
    private String largestAccountName;

    private List<BankAccountListResponseDto> bankAccounts;
    private List<AssetTrendPointDto> assetTrend;
    private List<AssetShareDto> assetShare;
}
