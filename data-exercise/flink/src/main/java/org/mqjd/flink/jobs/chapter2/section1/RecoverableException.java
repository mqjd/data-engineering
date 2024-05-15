package org.mqjd.flink.jobs.chapter2.section1;

import org.apache.flink.runtime.throwable.ThrowableAnnotation;
import org.apache.flink.runtime.throwable.ThrowableType;

@ThrowableAnnotation(ThrowableType.RecoverableError)
public class RecoverableException extends RuntimeException {

    public RecoverableException(String message) {
        super(message);
    }

}
