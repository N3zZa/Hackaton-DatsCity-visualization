package org.example.model.response.towerResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TowersResponse {
    public List<DoneTower> doneTowers;
    public double score;
    public CurrentTower tower;

    // Геттеры и сеттеры
}

