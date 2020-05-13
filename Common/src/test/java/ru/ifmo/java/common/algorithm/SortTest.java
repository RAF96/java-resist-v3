package ru.ifmo.java.common.algorithm;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortTest {

    @Test
    public void sort() {
        List<Double> numbers = Arrays.asList(3d, 2d, 4d, 5d, 1d);
        List<Double> list = new ArrayList<>(numbers);
        List<Double> res = Sort.sort(list);
        Collections.sort(list);
        assertEquals(res, list);
    }
}