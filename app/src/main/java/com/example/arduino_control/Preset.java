package com.example.arduino_control;

public class Preset {
    String name;
    int value1, value2, value3;

    public Preset(String name, int value1){
        this.name = name;
        this.value1 = value1;
    }

    public Preset(String name, int value1, int value2){
        this.name = name;
        this.value1 = value1;
        this.value2 = value2;
    }

    public Preset(String name, int value1, int value2, int value3) {
        this.name = name;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    public String getName() {
        return name;
    }

    public int getValue1() {
        return value1;
    }

    public int getValue2() {
        return value2;
    }

    public int getValue3() {
        return value3;
    }
}
