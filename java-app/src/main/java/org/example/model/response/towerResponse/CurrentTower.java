package org.example.model.response.towerResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class CurrentTower {
    public double score;
    public List<PlayerWord> words;
}
