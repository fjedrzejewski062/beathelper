package com.example.beathelper.enums;

public enum KeyType {

    // Major
    C_MAJOR("C Major"),
    C_SHARP_MAJOR("C Sharp Major"),
    D_MAJOR("D Major"),
    D_SHARP_MAJOR("D Sharp Major"),
    E_MAJOR("E Major"),
    F_MAJOR("F Major"),
    F_SHARP_MAJOR("F Sharp Major"),
    G_MAJOR("G Major"),
    G_SHARP_MAJOR("G Sharp Major"),
    A_MAJOR("A Major"),
    A_SHARP_MAJOR("A Sharp Major"),
    B_MAJOR("B Major"),

    // Minor
    C_MINOR("C Minor"),
    C_SHARP_MINOR("C Sharp Minor"),
    D_MINOR("D Minor"),
    D_SHARP_MINOR("D Sharp Minor"),
    E_MINOR("E Minor"),
    F_MINOR("F Minor"),
    F_SHARP_MINOR("F Sharp Minor"),
    G_MINOR("G Minor"),
    G_SHARP_MINOR("G Sharp Minor"),
    A_MINOR("A Minor"),
    A_SHARP_MINOR("A Sharp Minor"),
    B_MINOR("B Minor");

    private final String name;

    // Konstruktor enum
    KeyType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}