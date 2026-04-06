package pers.eloyhere.semantic;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println(Semantic.useRange(0, 7).translate(2).toLongStatistics().average());
    }
}
