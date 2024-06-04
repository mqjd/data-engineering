package org.mqjd.flink.utils.interceptor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.ExceptionHandler;

public class ExceptionSuppressHandler {

    @ExceptionHandler
    public static void onSuppress(@Binding.Throwable Throwable e, @Binding.Class Object clazz) {
        LogInterceptor.LOG.error("handler {} exception", clazz, e);
    }
}
