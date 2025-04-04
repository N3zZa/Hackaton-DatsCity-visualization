package org.example.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TowerWord {
    private int dir; // 1=Z, 2=X, 3=Y
    private int id;  // ID слова
    private int[] pos = new int[3]; // [x, y, z]
}