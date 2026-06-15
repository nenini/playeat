package com.nyamnyam.coach.diet.event;

import com.nyamnyam.coach.quest.service.QuestActivityVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class DietChangedEventListener {

    private static final Logger log = LoggerFactory.getLogger(DietChangedEventListener.class);

    private final QuestActivityVerificationService questActivityVerificationService;

    public DietChangedEventListener(QuestActivityVerificationService questActivityVerificationService) {
        this.questActivityVerificationService = questActivityVerificationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(DietChangedEvent event) {
        try {
            questActivityVerificationService.verifyDietActivity(event.userId(), event.activityDate());
        } catch (RuntimeException e) {
            log.warn("Failed to verify quest activity after diet change. userId={}, date={}",
                    event.userId(),
                    event.activityDate(),
                    e
            );
        }
    }
}
