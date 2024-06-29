package org.mqjd.flink.utils.interceptor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInterceptor {
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";
    public static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    @AtEnter(suppressHandler = ExceptionSuppressHandler.class)
    public static void atEnter(@Binding.Class Class<?> clz, @Binding.Method Method method,
        @Binding.Args Object[] args, @Binding.ArgNames String[] argNames) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        LOG.info(STR."[\{GREEN}Method Call\{RESET}] at {}.{}({}:{})", stackTraceElement.getClassName(),
            stackTraceElement.getMethodName(), stackTraceElement.getFileName(),
            stackTraceElement.getLineNumber());
    }

    @AtExit(suppressHandler = ExceptionSuppressHandler.class)
    public static void atExit(@Binding.Return Object returnObject) {
//        LOG.info("method return: {}", returnObject);
    }

    @AtExceptionExit(onException = RuntimeException.class, suppressHandler = ExceptionSuppressHandler.class)
    public static void atExceptionExit(@Binding.Throwable RuntimeException ex) {
        LOG.error("method exception", ex);
    }
}
