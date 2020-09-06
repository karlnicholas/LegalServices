package opca.repository;

import java.util.List;

import opca.model.StatuteCitation;
import opca.model.StatuteKey;

public interface IStatuteCitationDao {
    List<StatuteCitation> getAllStatuteCitations();
    StatuteCitation getStatuteCitationById(StatuteKey statuteKey);
    void addStatuteCitation(StatuteCitation statuteCitation);
    StatuteCitation mergeStatuteCitation(StatuteCitation statuteCitation);
    void deleteStatuteCitation(StatuteKey statuteKey);
    boolean statuteCitationExists(String title, String category);
}
