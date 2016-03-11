package model;

import java.util.HashMap;

public class Metric {
    public enum Type {
        DISTANCE, COSINE
    }
    private HashMap<Type, Double> metric;

    public Metric() {
        this.metric = new HashMap<>();
    }
    public Metric(double metric, Type type) {
        this.metric = new HashMap<>();
        this.metric.put(type, metric);
    }

    public double getMetric(Type type) {
        return metric.get(type);
    }

    public void addMetric(Type type, double metric) {
        this.metric.putIfAbsent(type, metric);
    }
}
