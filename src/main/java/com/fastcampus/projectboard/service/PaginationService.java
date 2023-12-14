package com.fastcampus.projectboard.service;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private final static int BAR_LENGTH = 5;

    public List<Integer> getPaginationBarNumbers(int curPageNumber, int totalPages) {
        int startNumber = Math.max(curPageNumber - (BAR_LENGTH / 2) , 0);
        int endNumber = Math.min(startNumber + BAR_LENGTH , totalPages) ;
        return IntStream.range(startNumber, endNumber).boxed().toList();
    }

    public int curBarLength() {
        return BAR_LENGTH;
    }
}
