package com.github.terma.gigaspacesqlconsole;

class EmptyValidator<T> implements Validator<T> {

    @Override
    public void validate(T object) throws Exception {
    }

}
