package org.example.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WordsResponse {
    private int[] mapSize;
    private int nextTurnSec;
    private String roundEndsAt;
    private int shuffleLeft;
    private int turn;
    private List<Integer> usedIndexes;
    private List<String> words;
}
