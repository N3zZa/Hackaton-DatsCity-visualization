package org.example.model.script;

import org.example.model.api.DatsCityApi;
import org.example.model.request.BuildRequest;
import org.example.model.response.WordsResponse;
import org.example.model.response.towerResponse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class TowerBuilder {
    private static final Logger logger = LoggerFactory.getLogger(TowerBuilder.class);

    private final DatsCityApi api;
    private List<String> currentWords;
    private List<Integer> usedWordIds;
    private int shuffleLeft;
    private int currentTurn;
    private int totalScore;
    private int totalTowers;

    public TowerBuilder(DatsCityApi api) {
        this.api = api;
    }

    public void playTurn() {
        try {
            currentTurn++;
            logger.info("\n=== Ход {} ===", currentTurn);

            // Получаем текущее состояние
            WordsResponse wordsResponse = api.getWords();
            updateWordState(wordsResponse);

            TowersResponse towersResponse = api.getTowers();
            CurrentTower currentTower = towersResponse.getTower();

            // Выводим статистику перед действием
            printStatistics(towersResponse);

            if (shouldShuffleWords()) {
                logger.info("-> Запрашиваем новый набор слов (осталось попыток: {})", shuffleLeft);
                wordsResponse = api.shuffleWords();
                updateWordState(wordsResponse);
            }

            if (shouldStartNewTower(currentTower)) {
                startNewTower();
            } else {
                extendExistingTower(currentTower);
            }

            // Обновляем и выводим итоговую статистику
            towersResponse = api.getTowers();
            printStatistics(towersResponse);
            updateGlobalStats(towersResponse);

        } catch (Exception e) {
            logger.error("Ошибка во время хода {}: {}", currentTurn, e.getMessage(), e);
        }
    }

    private void printStatistics(TowersResponse response) {
        logger.info("--- Текущая статистика ---");

        // Общая статистика
        logger.info("Общий счет: {}", response.getScore());

        int size = 0;
        if(response.getDoneTowers()!=null){
            size = response.getDoneTowers().size();
        }
        logger.info("Завершено башен: {}", size);

        // Статистика по текущей башне
        if (response.getTower() != null && response.getTower().getWords() != null) {
            CurrentTower tower = response.getTower();
            logger.info("Текущая башня:");
            logger.info("  - Этажей: {}", tower.getWords().size());
            logger.info("  - Счет: {}", tower.getScore());
            logger.info("  - Слов: {}", tower.getWords().size());
        } else {
            logger.info("Нет активной башни");
        }

        // Статистика по словам
        logger.info("Доступно слов: {}", currentWords.size());
        logger.info("Использовано слов: {}", usedWordIds.size());
        logger.info("Осталось перемешиваний: {}", shuffleLeft);
    }

    private void updateGlobalStats(TowersResponse response) {
        this.totalScore = (int) response.getScore();
        int size = 0;
        if(response.getDoneTowers()!=null){
            size = response.getDoneTowers().size();
        }
        this.totalTowers = size;
    }

    private void updateWordState(WordsResponse response) {
        this.currentWords = response.getWords();
        this.usedWordIds = response.getUsedIndexes();
        this.shuffleLeft = response.getShuffleLeft();
    }

    private boolean shouldShuffleWords() {
        if (shuffleLeft <= 0) return false;
        return currentWords.stream().filter(w -> w.length() >= 7).count() < 3;
    }

    private boolean shouldStartNewTower(CurrentTower tower) {
        return tower == null || tower.getWords() == null || tower.getWords().isEmpty();
    }

    private void startNewTower() {
        Optional<GameStrategy.WordPosition> foundation = GameStrategy.selectFoundationWord(currentWords, usedWordIds);
        if (foundation.isEmpty()) {
            logger.warn("Не найдено подходящего слова для основания");
            return;
        }

        BuildRequest request = new BuildRequest();
        request.setDone(false);
        request.setWords(List.of(foundation.get().word()));
        api.buildTower(request);

        logger.info("-> Создана новая башня с основанием (ID: {})", foundation.get().word().getId());
    }

    private void extendExistingTower(CurrentTower tower) {
        List<GameStrategy.WordPosition> newWords = GameStrategy.selectOptimalWords(currentWords, usedWordIds, tower.getWords());

        if (newWords.isEmpty()) {
            completeTower();
            return;
        }

        BuildRequest request = new BuildRequest();
        request.setDone(false);
        request.setWords(newWords.stream().map(GameStrategy.WordPosition::word).toList());
        api.buildTower(request);

        logger.info("-> Добавлено {} слов к башне", newWords.size());
    }

    private void completeTower() {
        BuildRequest request = new BuildRequest();
        request.setDone(true);
        request.setWords(List.of());
        api.buildTower(request);

        logger.info("-> Башня завершена");
    }

    public void printFinalStats() {
        logger.info("\n=== ИТОГОВАЯ СТАТИСТИКА ===");
        logger.info("Всего ходов: {}", currentTurn);
        logger.info("Общий счет: {}", totalScore);
        logger.info("Завершено башен: {}", totalTowers);
    }
}