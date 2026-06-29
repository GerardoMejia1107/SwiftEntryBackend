package com.gerardo.swiftentrybackend.domain.WaitingList.scheduler;

import com.gerardo.swiftentrybackend.domain.WaitingList.service.WaitingListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WaitingListScheduler {

    private final WaitingListService waitingListService;

    @Scheduled(fixedDelay = 60_000)
    public void expireStaleNotifications() {
        int count = waitingListService.expireNotifiedEntries();
        if (count > 0) {
            log.info("Expired {} waiting list notification(s) and re-queued next users where available.", count);
        }
    }
}
