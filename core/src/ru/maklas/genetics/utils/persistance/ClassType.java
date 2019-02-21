package ru.maklas.genetics.utils.persistance;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ClassType {

    Class clazz();

}
