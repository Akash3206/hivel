import org.json.JSONObject;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolynomialSolver {

    public static void main(String[] args) throws Exception {

        String path = args.length > 0 ? args[0] : "src/main/resources/input.json";

        String jsonStr = new String(Files.readAllBytes(Paths.get(path)));
        JSONObject root = new JSONObject(jsonStr);

        // Read k (minimum roots needed)
        JSONObject keysObj = root.getJSONObject("keys");
        int k = keysObj.getInt("k");

        // Collect numeric keys
        List<Integer> numericKeys = new ArrayList<>();
        for (String key : root.keySet()) {
            if (key.equals("keys")) continue;
            try {
                numericKeys.add(Integer.parseInt(key));
            } catch (Exception ignored) {}
        }
        Collections.sort(numericKeys);

        // Decode first k points
        List<BigInteger> x = new ArrayList<>();
        List<BigInteger> y = new ArrayList<>();

        for (int i = 0; i < numericKeys.size() && x.size() < k; i++) {
            String key = numericKeys.get(i).toString();
            JSONObject entry = root.getJSONObject(key);

            int base = Integer.parseInt(entry.getString("base"));
            String value = entry.getString("value");

            BigInteger xi = BigInteger.valueOf(x.size() + 1);
            BigInteger yi = new BigInteger(value, base);

            x.add(xi);
            y.add(yi);
        }

        // Compute c = f(0)
        System.out.println(lagrangeAtZero(x, y));
    }

    // Lagrange interpolation at x = 0
    private static BigInteger lagrangeAtZero(List<BigInteger> x, List<BigInteger> y) {

        int k = x.size();
        BigInteger finalNum = BigInteger.ZERO;
        BigInteger finalDen = BigInteger.ONE;

        for (int i = 0; i < k; i++) {

            BigInteger xi = x.get(i);
            BigInteger yi = y.get(i);

            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger xj = x.get(j);
                    num = num.multiply(xj.negate());
                    den = den.multiply(xi.subtract(xj));
                }
            }

            BigInteger termNum = yi.multiply(num);
            BigInteger termDen = den;

            finalNum = finalNum.multiply(termDen).add(termNum.multiply(finalDen));
            finalDen = finalDen.multiply(termDen);
        }

        return finalNum.divide(finalDen);
    }
}
