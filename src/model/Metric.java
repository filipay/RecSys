package model;

import java.util.HashMap;

public class Metric {
    public enum Type {
        DISTANCE, COSINE, PEARSON
    }
    private HashMap<Type, Double> metric;

    public Metric() {
        this.metric = new HashMap<>();
    }
    Metric(double metric, Type type) {
        this.metric = new HashMap<>();
        this.metric.put(type, metric);
    }

    double getMetric(Type type) {
        return metric.get(type);
    }

    void addMetric(Type type, double metric) {
        this.metric.putIfAbsent(type, metric);
    }
}
