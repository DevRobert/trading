package trading.challenges;

import java.util.ArrayList;
import java.util.List;

public class ParameterGenerators {
    public static List<Object> getHighResolutionDoubles(double inclusiveMin, double inclusiveMax) {
        List<Object> result = new ArrayList<>();

        addValue(result, 0.0, inclusiveMin, inclusiveMax);

        for(double value = 0.0001; value < 0.001; value += 0.00002) {
            addValue(result, value, inclusiveMin, inclusiveMax);
        }

        for(double value = 0.001; value < 0.01; value += 0.0002) {
            addValue(result, value, inclusiveMin, inclusiveMax);
        }

        for(double value = 0.01; value < 0.1; value += 0.002) {
            addValue(result, value, inclusiveMin, inclusiveMax);
        }

        for(double value= 0.1; value <= 0.2; value += 0.02) {
            addValue(result, value, inclusiveMin, inclusiveMax);
        }

        return result;
    }

    public static List<Object> getLowResolutionDoubles(double inclusiveMin, double inclusiveMax) {
        List<Object> result = new  ArrayList<>();

        addValue(result, 0.0, inclusiveMin, inclusiveMax);

        for(double value = 0.0001; value < 0.001; value += 0.0001) {
            addValue(result, value, inclusiveMin, inclusiveMax);
        }

        for(double value = 0.001; value < 0.01; value += 0.001) {
            addValue(result, value, inclusiveMin, inclusiveMax);
        }

        for(double value = 0.01; value < 0.1; value += 0.01) {
            addValue(result, value, inclusiveMin, inclusiveMax);
        }

        for(double value= 0.1; value <= 1; value += 0.1) {
            addValue(result, value, inclusiveMin, inclusiveMax);
        }

        return result;
    }

    public static List<Object> getVeryLowResolutionDoubles(double inclusiveMin, double inclusiveMax) {
        List<Object> result = new  ArrayList<>();

        addValue(result, 0.0, inclusiveMin, inclusiveMax);

        for(double value = 0.001; value < 0.01; value += 0.001) {
            addValue(result, value, inclusiveMin, inclusiveMax);
        }

        for(double value = 0.01; value < 0.1; value += 0.01) {
            addValue(result, value, inclusiveMin, inclusiveMax);
        }

        for(double value = 0.1; value <= 1; value += 0.1) {
            addValue(result, value, inclusiveMin, inclusiveMax);
        }

        return result;
    }

    private static void addValue(List<Object> result, double value, double inclusiveMin, double inclusiveMax) {
        if(value >= inclusiveMin && value <= inclusiveMax) {
            result.add(value);
        }
    }
}
