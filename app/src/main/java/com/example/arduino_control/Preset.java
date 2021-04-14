package com.example.arduino_control;

import java.io.Serializable;

/**
 * Class which is responsible for presets. Class have three parameters of knobs value and preset name.
 * @author Vikinn
 */
public class Preset implements Serializable {
    String name;
    int value1, value2, value3;

    /**
     * Empty constructor due to Serializable object
     * @see Serializable
     */
    public Preset(){}

    /**
     * Constructor for one knob preset
     * @param name Preset name
     * @param value1 First knob value
     */
    public Preset(String name, int value1){
        this.name = name;
        this.value1 = value1;
    }

    /**
     * Constructor for two knobs preset
     * @param name Preset Name
     * @param value1 First knob value
     * @param value2 Second knob value
     */
    public Preset(String name, int value1, int value2){
        this.name = name;
        this.value1 = value1;
        this.value2 = value2;
    }

    /**
     * Constructor for three knobs preset
     * @param name Preset Name
     * @param value1 First knob value
     * @param value2 Second knob value
     * @param value3 Third knob value
     */
    public Preset(String name, int value1, int value2, int value3) {
        this.name = name;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    public void setName(String name) {
        this.name = name;
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
