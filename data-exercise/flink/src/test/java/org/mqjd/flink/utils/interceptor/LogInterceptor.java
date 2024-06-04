package org.mqjd.flink.utils.interceptor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import java.lang.reflect.Method;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInterceptor {

    public static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    @AtEnter(suppressHandler = ExceptionSuppressHandler.class)
    public static void atEnter(@Binding.Class Class<?> clz, @Binding.Method Method method,
        @Binding.Args Object[] args, @Binding.ArgNames String[] argNames) {
        String[] paramDesc = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            paramDesc[i] = String.format("%s %s", args[i].getClass().getSimpleName(), argNames[i]);
        }
        String returnType = Optional.of(method.getReturnType()).map(Class::getSimpleName)
            .orElse("void");
        LOG.info("method invoke: {} {}.{}({})", returnType, clz.getSimpleName(), method.getName(),
            String.join(" ,", paramDesc));
    }

    @AtExit(suppressHandler = ExceptionSuppressHandler.class)
    public static void atExit(@Binding.Return Object returnObject) {
        LOG.info("method return: {}", returnObject);
    }

    @AtExceptionExit(onException = RuntimeException.class, suppressHandler = ExceptionSuppressHandler.class)
    public static void atExceptionExit(@Binding.Throwable RuntimeException ex) {
        LOG.error("method exception", ex);
    }
}
