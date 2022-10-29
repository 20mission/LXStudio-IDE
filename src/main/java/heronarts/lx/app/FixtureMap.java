package heronarts.lx.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixtureMap {

    static int pointsPerStrip = 180;
    static int totalStrips = 8;
    public static int[] maxCoords = {pointsPerStrip, totalStrips};
    static int[] stripLengths = { 170, 10 };
    public static List<List<Integer>> getStrips() {
        return Arrays.asList(
                Arrays.asList(0, 1),
                Arrays.asList(2, 3),
                Arrays.asList(4, 5),
                Arrays.asList(6, 7),
                Arrays.asList(8, 9),
                Arrays.asList(10, 11),
                Arrays.asList(12, 13),
                Arrays.asList(14, 15)
        );
    }

    // returns an array of the format [fixture number, led index]
    // so if you want to get the 3rd led in the 2nd fixture, you'd get [1, 2]
    public static List<Integer> getPixelAt(int x, int y) {
        if (x < 0 || x >= maxCoords[0] || y < 0 || y >= maxCoords[1]) {
            System.out.println("Invalid coordinates: " + x + ", " + y);
            return null;
        }
        List<List<Integer>> strips = getStrips();
        List<Integer> row = strips.get(y);
        int colIndex = 0;
        int colCumulative = 0;
        int ledIndex = 0;
        for (int i = 0; i < stripLengths.length; i++) {
            colCumulative += stripLengths[i];
            if (x < colCumulative) {
                colIndex = i;
                ledIndex = x - (colCumulative - stripLengths[i]);
                break;
            }
        }
        int rowFixtureNumber = row.get(colIndex);

        return Arrays.asList(rowFixtureNumber, ledIndex);
    }

}
