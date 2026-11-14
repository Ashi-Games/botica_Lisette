package com.botica.lisette.ia;

public class IAException extends RuntimeException {
    public IAException(String mensaje, Throwable causa) { super(mensaje, causa); }
}