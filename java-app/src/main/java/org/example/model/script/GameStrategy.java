package org.example.model.script;

import org.example.model.request.TowerWord;
import org.example.model.response.towerResponse.PlayerWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class GameStrategy {
    private static final Logger logger = LoggerFactory.getLogger(GameStrategy.class);

    public record WordPosition(TowerWord word, int score) {}

    public static Optional<WordPosition> selectFoundationWord(List<String> words, List<Integer> usedIds) {
        if (words == null || words.isEmpty()) {
            logger.warn("Передан пустой список слов");
            return Optional.empty();
        }

        // Создаем список доступных слов с их индексами
        Map<Integer, String> availableWords = new HashMap<>();
        for (int i = 0; i < words.size(); i++) {
            if (!usedIds.contains(i)) {
                availableWords.put(i, words.get(i));
            }
        }

        if (availableWords.isEmpty()) {
            logger.warn("Нет доступных слов для основания");
            return Optional.empty();
        }

        // Находим самое длинное доступное слово
        Map.Entry<Integer, String> longestEntry = availableWords.entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().length()))
                .orElseThrow();

        int wordId = longestEntry.getKey();
        String word = longestEntry.getValue();

        // Создаем слово для основания (Z-направление)
        TowerWord foundationWord = new TowerWord();
        foundationWord.setDir(1); // Z-направление
        foundationWord.setId(wordId);
        foundationWord.setPos(new int[]{0, 0, 0});

        return Optional.of(new WordPosition(foundationWord, word.length() * 2));
    }

    public static List<WordPosition> selectOptimalWords(
            List<String> words,
            List<Integer> usedIds,
            List<PlayerWord> existingTower
    ) {
        if (words == null || words.isEmpty()) {
            logger.warn("Передан пустой список слов");
            return Collections.emptyList();
        }

        if (existingTower == null || existingTower.isEmpty()) {
            logger.warn("Передана пустая башня");
            return Collections.emptyList();
        }

        // Создаем словарь доступных слов
        Map<Integer, String> availableWords = new HashMap<>();
        for (int i = 0; i < words.size(); i++) {
            if (!usedIds.contains(i)) {
                availableWords.put(i, words.get(i));
            }
        }

        List<WordPosition> candidates = new ArrayList<>();

        // Анализируем топ-10 самых длинных доступных слов
        availableWords.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().length(), e1.getValue().length()))
                .limit(10)
                .forEach(entry -> {
                    int wordId = entry.getKey();
                    String word = entry.getValue();

                    // Ищем лучшее пересечение с существующими словами
                    for (PlayerWord existing : existingTower) {
                        if (canConnect(existing, word)) {
                            int[] pos = calculatePosition(existing, word);
                            int dir = getPerpendicularDirection(existing.getDir());
                            int score = calculateScore(word, existingTower.size());

                            TowerWord newWord = new TowerWord();
                            newWord.setDir(dir);
                            newWord.setId(wordId);
                            newWord.setPos(pos);

                            candidates.add(new WordPosition(newWord, score));
                            break;
                        }
                    }
                });

        // Возвращаем топ-3 лучших варианта
        return candidates.stream()
                .sorted(Comparator.comparingInt(WordPosition::score).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    private static boolean canConnect(PlayerWord existing, String newWord) {
        if (existing == null || existing.getText() == null || newWord == null) {
            return false;
        }

        String existingText = existing.getText();
        return existingText.chars()
                .anyMatch(c -> newWord.indexOf(c) >= 0);
    }

    private static int[] calculatePosition(PlayerWord existing, String newWord) {
        int[] pos = Arrays.copyOf(existing.getPos(), existing.getPos().length);
        pos[2] -= 1; // Следующий этаж
        return pos;
    }

    private static int getPerpendicularDirection(int existingDir) {
        return existingDir == 1 ? 2 : 1; // Чередуем направления X/Y
    }

    private static int calculateScore(String word, int towerHeight) {
        return word.length() * (towerHeight + 1);
    }
}