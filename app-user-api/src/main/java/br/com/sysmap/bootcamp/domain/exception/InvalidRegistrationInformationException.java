package br.com.sysmap.bootcamp.domain.exception;

public class InvalidRegistrationInformationException extends RuntimeException{

    public InvalidRegistrationInformationException(String message){
        super(message);
    }
}
