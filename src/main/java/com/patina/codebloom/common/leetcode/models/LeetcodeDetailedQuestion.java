package com.patina.codebloom.common.leetcode.models;

public class LeetcodeDetailedQuestion {

    private int runtime;
    private String runtimeDisplay;
    private float runtimePercentile;
    private int memory;
    private String memoryDisplay;
    private float memoryPercentile;
    private String code;
    private Lang lang;

    public LeetcodeDetailedQuestion(
        final int runtime,
        final String runtimeDisplay,
        final float runtimePercentile,
        final int memory,
        final String memoryDisplay,
        final float memoryPercentile,
        final String code,
        final Lang lang
    ) {
        this.runtime = runtime;
        this.runtimeDisplay = runtimeDisplay;
        this.runtimePercentile = runtimePercentile;
        this.memory = memory;
        this.memoryDisplay = memoryDisplay;
        this.memoryPercentile = memoryPercentile;
        this.code = code;
        this.lang = lang;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(final int runtime) {
        this.runtime = runtime;
    }

    public String getRuntimeDisplay() {
        return runtimeDisplay;
    }

    public void setRuntimeDisplay(final String runtimeDisplay) {
        this.runtimeDisplay = runtimeDisplay;
    }

    public float getRuntimePercentile() {
        return runtimePercentile;
    }

    public void setRuntimePercentile(final float runtimePercentile) {
        this.runtimePercentile = runtimePercentile;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(final int memory) {
        this.memory = memory;
    }

    public String getMemoryDisplay() {
        return memoryDisplay;
    }

    public void setMemoryDisplay(final String memoryDisplay) {
        this.memoryDisplay = memoryDisplay;
    }

    public float getMemoryPercentile() {
        return memoryPercentile;
    }

    public void setMemoryPercentile(final float memoryPercentile) {
        this.memoryPercentile = memoryPercentile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(final Lang lang) {
        this.lang = lang;
    }
}
