package model;

import java.util.HashMap;

/**
 * Author: Filip Piskor[12331436] on 20/02/16.
 */
public class Dataset {
    protected HashMap<Integer, User> users;
    protected HashMap<Integer, Item> items;

    protected double rootMeanSquaredError(Double a, Double b) {
        return Math.sqrt(Math.pow(b - a, 2));
    }
    protected double rootMeanSquaredError(Integer a, Integer b) {
        return Math.sqrt(Math.pow(b - a, 2));
    }

    protected double coverage(Integer canPredict, Integer totalItems) {
        return canPredict/totalItems;
    }
    public Dataset(HashMap<Integer, User> users, HashMap<Integer, Item> items) {
        this.users = users;
        this.items = items;
    }



    public User getUser(Integer userID) {
        return users.get(userID);
    }
    public Item getItem(Integer itemID) {
        return items.get(itemID);
    }
}
