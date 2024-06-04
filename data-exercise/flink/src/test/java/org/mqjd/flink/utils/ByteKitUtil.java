package org.mqjd.flink.utils;

import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.utils.AgentUtils;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import java.lang.instrument.Instrumentation;
import java.util.List;
import org.mqjd.flink.utils.interceptor.LogInterceptor;

public class ByteKitUtil {

    private static Instrumentation instrumentation;

    private static void install() {
        if (instrumentation == null) {
            instrumentation = AgentUtils.install();
        }
    }

    public static void interceptWithLog(Class<?> clz, String name) throws Exception {
        install();
        DefaultInterceptorClassParser interceptorClassParser = new DefaultInterceptorClassParser();
        List<InterceptorProcessor> processors = interceptorClassParser.parse(LogInterceptor.class);
        ClassNode classNode = AsmUtils.loadClass(clz);
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(name)) {
                MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);
                for (InterceptorProcessor interceptor : processors) {
                    interceptor.process(methodProcessor);
                }
            }
        }
        byte[] bytes = AsmUtils.toBytes(classNode);
        AgentUtils.reTransform(clz, bytes);
    }

}
