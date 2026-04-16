package chdaeseung.accountbook.recurring.scheduler;

import chdaeseung.accountbook.recurring.service.RecurringSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecurringScheduler {

    private final RecurringSchedulerService recurringSchedulerService;

    @Scheduled(cron = "0 0 0 * * *")
    public void daily() {
        recurringSchedulerService.generateTodayRecurringTransactions();
    }
}
