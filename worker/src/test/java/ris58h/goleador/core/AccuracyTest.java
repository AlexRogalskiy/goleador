package ris58h.goleador.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class AccuracyTest {
    static void runFor(List<List<?>> parameterLists, Function<List<?>, Comparable> testFunction) {
        runFor(parameterLists, testFunction, false);
    }
    static void runFor(List<List<?>> parameterLists,
                       Function<List<?>, Comparable> testFunction,
                       boolean reversed) {
        List<List<?>> cartesianProduct = cartesianProduct(parameterLists);
        if (cartesianProduct.isEmpty()) {
            throw new RuntimeException("No parameter provided!");
        }
        List<Result> results = new ArrayList<>();
        for (List<?> params : cartesianProduct) {
            try {
                long before = System.currentTimeMillis();
                Comparable result = testFunction.apply(params);
                long testTimeMills = System.currentTimeMillis() - before;
                results.add(new Result(result, params, testTimeMills));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (results.isEmpty()) {
            System.out.println("No results!");
        } else {
            Comparator<Result> valueComparator = Comparator.comparing(r -> r.value);
            Comparator<Result> resultComparator = reversed ? valueComparator.reversed() : valueComparator;
            results.sort(resultComparator.reversed()); // we want best results printed last
            results.forEach(AccuracyTest::printResult);
        }
    }

    private static void printResult(Result result) {
        System.out.println(String.format(
                "%s for %s in %d seconds",
                result.value.toString(),
                result.params.toString(),
                result.timeMills / 1000
        ));
    }

    private static class Result {
        final Comparable value;
        final List<?> params;
        final long timeMills;

        public Result(Comparable value, List<?> params, long timeMills) {
            this.value = value;
            this.params = params;
            this.timeMills = timeMills;
        }
    }

    private static List<List<?>> cartesianProduct(List<List<?>> lists) {
        List<List<?>> resultLists = new ArrayList<>();
        if (lists.isEmpty()) {
            resultLists.add(Collections.emptyList());
            return resultLists;
        }
        List<?> firstList = lists.get(0);
        List<List<?>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
        for (Object item : firstList) {
            for (List<?> remainingList : remainingLists) {
                ArrayList<Object> resultList = new ArrayList<>();
                resultList.add(item);
                resultList.addAll(remainingList);
                resultLists.add(resultList);
            }
        }
        return resultLists;
    }
}
