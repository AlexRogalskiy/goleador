package ris58h.goleador.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class AccuracyTest {
    static void runFor(List<List<?>> parameterLists, Function<List<?>, Comparable> testFunction) {
        List<List<?>> cartesianProduct = cartesianProduct(parameterLists);
        Comparable bestResult = null;
        List<?> bestParams = null;
        if (cartesianProduct.isEmpty()) {
            throw new RuntimeException("No parameter provided!");
        }
        for (List<?> params : cartesianProduct) {
            try {
                Comparable result = testFunction.apply(params);
                print(params, result);
                if (bestResult == null || result.compareTo(bestResult) < 0) {
                    bestResult = result;
                    bestParams = params;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bestResult != null) {
            System.out.print("Best: ");
            print(bestParams, bestResult);
        } else {
            System.out.println("No result!");
        }
    }

    private static void print(List<?> params, Comparable result) {
        System.out.println(String.format(
                "%s for %s",
                result.toString(),
                params.toString()
        ));
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
