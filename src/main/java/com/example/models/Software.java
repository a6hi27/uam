package com.example.models;

import java.util.List;

public class Software {
    private int id;
    private String name;
    private List<String> accessLevels;

    public Software(int id, String name, List<String> accessLevels) {
        this.id = id;
        this.name = name;
        this.accessLevels = accessLevels;
    }

    public String getName() {
        return name;
    }

    public List<String> getAccessLevels() {
        return accessLevels;
    }

    public int getId() {
        return id;
    }
}

