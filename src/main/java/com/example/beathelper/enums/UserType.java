package com.example.beathelper.enums;

public enum UserType {
    ARTIST("ARTIST"),
    BEATMAKER("BEATMAKER");

    private final String name;

    UserType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
