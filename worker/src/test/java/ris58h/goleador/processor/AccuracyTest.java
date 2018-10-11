package ris58h.goleador.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class AccuracyTest {
    static void runFor(List<List<?>> parameterLists, Function<List<?>, Number> testFunction) {
        List<List<?>> cartesianProduct = cartesianProduct(parameterLists);
        if (cartesianProduct.isEmpty()) {
            throw new RuntimeException("No parameter provided!");
        }
        ParamsAndResult best = cartesianProduct.stream()
                .map(params -> new ParamsAndResult(params, testFunction.apply(params)))
                .peek(AccuracyTest::print)
                .sorted(Comparator.comparing(par -> par.result.doubleValue()))
                .iterator()
                .next();
        System.out.print("Best: ");
        print(best);
    }

    private static void print(ParamsAndResult paramsAndResult) {
        System.out.println(String.format(
                "%s for %s",
                paramsAndResult.result.toString(),
                paramsAndResult.params.toString()
        ));
    }

    private static class ParamsAndResult {
        final List<?> params;
        final Number result;

        public ParamsAndResult(List<?> params, Number result) {
            this.params = params;
            this.result = result;
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
