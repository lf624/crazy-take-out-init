package com.crazy;

import com.crazy.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class CrazyTest {

    @Test
    public void testOther() {
        List<String> ints = Arrays.asList("1", "2", "3", "4");
        System.out.println(ints.stream().collect(Collectors.joining(",")));
    }
}
