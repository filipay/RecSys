package tests;

import model.Item;
import model.Metric;
import model.Similarity;
import model.User;
import utils.Loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: Filip Piskor[12331436] on 10/03/16.
 */
public class CosineSimilarity extends Similarity {

    protected CosineSimilarity(HashMap<Integer, User> users, HashMap<Integer, Item> items) {
        super(users, items);
    }

    @Override
    public Double computeSimilarity(User u1, User u2) {
        double top = 0, bottom;
        double sumA = 0, sumB = 0;
        for (Integer itemID : u1.getCorated(u2)) {
            double a = u1.getRating(itemID), b = u2.getRating(itemID);
            top +=  a * b;
            sumA += a * a;
            sumB += b * b;
        }

        bottom = Math.sqrt(sumA) * Math.sqrt(sumB);

        return top / bottom;
    }

    public static void main(String[] args) throws IOException {
        int MIN_CORATED = 10;
        int SIZE = 10;
        ArrayList<String> lines = new ArrayList<>();

        long start = System.currentTimeMillis();
        lines.add("minCorated, size, coverage, meanRMSE");

        for (int stepCorated = 1; stepCorated < 11; stepCorated++) {
            int currMinCorated = MIN_CORATED * stepCorated;

            CosineSimilarity cs = new CosineSimilarity(Loader.loadUsers(), Loader.loadItems());
            for (int stepSize = 1; stepSize < 11; stepSize++) {

                int currSize = SIZE * stepSize;

                System.out.println("minCorated: " + currMinCorated + ", size: " + currSize);

                Result result = cs.test(currMinCorated, currSize, Metric.Type.COSINE);
                cs.resetAllNeighbourhoods();

                lines.add(currMinCorated + ", " + currSize + ", " + result.getCoverage() + ", " + result.getMeanRMSE());
            }
            lines.add("");
        }

        Files.write(Paths.get("cos_sim_"+ MIN_CORATED * SIZE +".csv"),lines);
        long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start));
    }
}
