package ris58h.goleador.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                String paramsString = String.join(", ",
                        params.stream().map(Object::toString).collect(Collectors.toList()));
                System.out.println("Run for params: " + paramsString);
                long before = System.currentTimeMillis();
                Comparable result = testFunction.apply(params);
                long testTimeMills = System.currentTimeMillis() - before;
                System.out.println("Run ended in " + (testTimeMills / 1000) + " seconds with result " + result);
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
