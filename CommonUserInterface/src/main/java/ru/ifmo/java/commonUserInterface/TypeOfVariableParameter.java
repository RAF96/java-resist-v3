package ru.ifmo.java.commonUserInterface;

public enum TypeOfVariableParameter {
    SIZE_OF_REQUEST,
    NUMBER_OF_CLIENTS,
    CLIENT_SLEEP_TIME;

    public static class UnknownTypeOfVariableParameter extends RuntimeException {
    }
}
