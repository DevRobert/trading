package trading.application;

import trading.domain.strategy.compound.Scores;

public interface ScoringService {
    Scores getCurrentScoring();
}
