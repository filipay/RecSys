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
 * Author: Filip Piskor[12331436] on 28/03/16.
 */
public class ResnicksFormulaPrediction extends Similarity {
    protected ResnicksFormulaPrediction(HashMap<Integer, User> users, HashMap<Integer, Item> items) {
        super(users, items);
    }

    @Override
    public Double computeSimilarity(User u1, User u2) {
        Double u1MeanRating = u1.meanRating();
        Double u2MeanRating = u2.meanRating();

        Double top = 0.0, bottom = 0.0;

        for (Integer itemID : u1.getCorated(u2)) {

            Double a = u1.getRating(itemID) - u1MeanRating;
            Double b = u2.getRating(itemID) - u2MeanRating;

            top += a * b;

            bottom += Math.sqrt(a * a  * b * b);

        }
        return top/bottom;
    }

    @Override
    protected double prediction(Integer userID, Integer itemID, Metric.Type type) {
        User user = getUser(userID);
        Double top = 0.0, bottom = 0.0;
        for (User neighbour : user.getNeighbourhood()) {
            if (neighbour.hasRating(itemID)) {

                Double meanDeviation = neighbour.getRating(itemID) - neighbour.meanRating();
                Double similarity = user.getMetricToUser(neighbour, type);

                top += meanDeviation * similarity;

                bottom += Math.abs(similarity);

            }
        }
        return user.meanRating() + top/bottom;
    }

    public static void main(String[] args) throws IOException {
        int MIN_CORATED = 10;
        int SIZE = 10;

        long averageRuntime = 0;
        ArrayList<String> lines = new ArrayList<>();

        lines.add("minCorated, size, coverage, meanRMSE");

        for (int stepCorated = 1; stepCorated < 11; stepCorated++) {
            int currMinCorated = MIN_CORATED * stepCorated;
            long start = System.currentTimeMillis();

            ResnicksFormulaPrediction rfp = new ResnicksFormulaPrediction(Loader.loadUsers(), Loader.loadItems());
            for (int stepSize = 1; stepSize < 11; stepSize++) {

                int currSize = SIZE * stepSize;

                System.out.println("minCorated: " + currMinCorated + ", size: " + currSize);

                Result result = rfp.test(currMinCorated, currSize, Metric.Type.PEARSON);
                rfp.resetAllNeighbourhoods();

                lines.add(currMinCorated + ", " + currSize + ", " + result.getCoverage() + ", " + result.getMeanRMSE());
            }
            long end = System.currentTimeMillis();
            averageRuntime += (end-start);
            lines.add(currMinCorated + ", "+(end - start)/10.0);
        }

        Files.write(Paths.get("resnick_"+ MIN_CORATED * SIZE +".csv"),lines);
        System.out.println("Total average runtime: " + averageRuntime/100);
    }
}
