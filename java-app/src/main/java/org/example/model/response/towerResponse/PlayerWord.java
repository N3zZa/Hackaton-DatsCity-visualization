package org.example.model.response.towerResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
 public class PlayerWord {
    public int dir;
    public int[] pos;
    public String text;
}
