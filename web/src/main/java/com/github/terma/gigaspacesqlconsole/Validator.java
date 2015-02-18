package com.github.terma.gigaspacesqlconsole;

public interface Validator<T> {

    void validate(T object) throws Exception;

}
