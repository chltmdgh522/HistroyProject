package history.history.visitant.domain.service;


import history.history.visitant.domain.repository.VisitRepository;
import history.history.visitant.domain.visit.Visitant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;

    public Optional<Visitant> addService() {
        Optional<Visitant> visit = visitRepository.findVisit(1);
        visitRepository.updateTotal(visit.get().getTotal() + 1);
        visitRepository.updateToday(visit.get().getToday() + 1);
        return visit;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void cronCall() {
        visitRepository.updateToday(0);
    }
}
