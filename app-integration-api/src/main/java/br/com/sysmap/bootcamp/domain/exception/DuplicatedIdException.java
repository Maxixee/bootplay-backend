package br.com.sysmap.bootcamp.domain.exception;

public class DuplicatedIdException extends RuntimeException{

    public DuplicatedIdException(String message){
        super(message);
    }
}
