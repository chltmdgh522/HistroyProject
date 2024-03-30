package history.history.visitant.domain.repository;


import history.history.visitant.domain.visit.Visitant;

import java.util.Optional;

public interface VisitRepository {


    void updateTotal(int total);

    void updateToday(int today);

    Optional<Visitant> findVisit(int id);

}
