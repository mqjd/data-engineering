package org.mqjd.flink.utils;

import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.utils.AgentUtils;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.deps.org.objectweb.asm.Opcodes;
import com.alibaba.deps.org.objectweb.asm.Type;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.Optional;
import org.mqjd.flink.utils.interceptor.LogInterceptor;

public class ByteKitUtil {

    private static Instrumentation instrumentation;

    private static void install() {
        if (instrumentation == null) {
            instrumentation = AgentUtils.install();
        }
    }

    public static void interceptWithLog(Class<?> clz, String... methodDeclarations)
        throws Exception {
        install();
        DefaultInterceptorClassParser interceptorClassParser = new DefaultInterceptorClassParser();
        List<InterceptorProcessor> processors = interceptorClassParser.parse(LogInterceptor.class);
        ClassNode classNode = AsmUtils.loadClass(clz);

        for (MethodNode methodNode : classNode.methods) {
            String methodDec = getMethodDeclaration(clz, methodNode);
            for (String methodDeclaration : methodDeclarations) {
                if (methodDec.contains(simpleMethodDeclaration(methodDeclaration))) {
                    MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);
                    for (InterceptorProcessor interceptor : processors) {
                        interceptor.process(methodProcessor);
                    }
                }
            }
        }
        byte[] bytes = AsmUtils.toBytes(classNode);
        AgentUtils.reTransform(clz, bytes);
    }

    public static String simpleMethodDeclaration(String methodDeclaration) {
        return methodDeclaration.replaceAll(", ", ",");
    }

    public static String simpleDescriptor(String desc) {
        return internalizeDescriptor(desc).replaceAll("\\w+\\.", "").replaceAll("<\\*>", "<?>")
            .replaceAll(",\\)", ")").replaceAll(", ", ",").replaceAll(",>", ">");
    }

    public static String internalizeDescriptor(String desc) {
        StringBuilder buffer = new StringBuilder();
        String sepr = "";
        int argStart = desc.indexOf('(');
        int argEnd = desc.indexOf(')');
        int max = desc.length();
        if (argStart < 0 || argEnd < 0) {
            return "(...)";
        }
        int arrayCount = 0;
        boolean addSepr = false;

        buffer.append("(");

        for (int idx = argStart + 1; idx < max; ) {
            char next = desc.charAt(idx);
            if (addSepr) {
                while (arrayCount > 0) {
                    buffer.append("[]");
                    arrayCount--;
                }
                buffer.append(sepr);
            }
            addSepr = true;
            switch (next) {
                case '>': {
                    buffer.append(">");
                }
                break;
                case 'B': {
                    buffer.append("byte");
                }
                break;
                case 'C': {
                    buffer.append("char");
                }
                break;
                case 'S': {
                    buffer.append("short");
                }
                break;
                case 'I': {
                    buffer.append("int");
                }
                break;
                case 'J': {
                    buffer.append("long");
                }
                break;
                case 'Z': {
                    buffer.append("boolean");
                }
                break;
                case 'F': {
                    buffer.append("float");
                }
                break;
                case 'D': {
                    buffer.append("double");
                }
                case 'V': {
                    buffer.append("void");
                }
                break;
                case 'L': {
                    int tailIdx = idx + 1;
                    while (tailIdx < max) {
                        char tailChar = desc.charAt(tailIdx);
                        if (tailChar == ';') {
                            break;
                        }
                        if (tailChar == '/') {
                            tailChar = '.';
                        }
                        buffer.append(tailChar);
                        tailIdx++;
                    }
                    idx = tailIdx;
                }
                break;
                case '[': {
                    arrayCount++;
                    addSepr = false;
                }
                break;
                case ')': {
                    if (idx == argEnd - 1) {
                        buffer.append(")");
                    } else {
                        // leave room for return type
                        buffer.append(") ");
                    }
                    addSepr = false;
                }
                break;
                default: {
                    addSepr = false;
                }
            }
            idx++;
            if (idx < argEnd) {
                sepr = ",";
            } else {
                sepr = "";
            }
        }

        return buffer.toString();
    }


    private static String getMethodDeclaration(Class<?> clz, MethodNode methodNode) {
        int access = methodNode.access;
        StringBuilder sb = new StringBuilder(128);
        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            sb.append("public ");
        }
        if ((access & Opcodes.ACC_PRIVATE) != 0) {
            sb.append("private ");
        }
        if ((access & Opcodes.ACC_PROTECTED) != 0) {
            sb.append("protected ");
        }
        if ((access & Opcodes.ACC_STATIC) != 0) {
            sb.append("static ");
        }

        if ((access & Opcodes.ACC_FINAL) != 0) {
            sb.append("final ");
        }
        if ((access & Opcodes.ACC_SYNCHRONIZED) != 0) {
            sb.append("synchronized ");
        }
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            sb.append("native ");
        }
        if ((access & Opcodes.ACC_ABSTRACT) != 0) {
            sb.append("abstract ");
        }

        String paramAndReturnType = simpleDescriptor(
            Optional.ofNullable(methodNode.signature).orElse(methodNode.desc));

        int index = paramAndReturnType.indexOf(')');
        String param = paramAndReturnType.substring(0, index + 1).trim();
        String returnType = paramAndReturnType.substring(index + 1).trim();

        // skip constructor return type
        if (methodNode.name.equals("<init>")) {
            sb.append(clz.getName());
        } else {
            sb.append(returnType).append(' ');
            sb.append(methodNode.name);
        }

        sb.append(param);

        if (methodNode.exceptions != null) {
            int exceptionSize = methodNode.exceptions.size();
            if (exceptionSize > 0) {
                sb.append(" throws");
                for (int i = 0; i < exceptionSize; ++i) {
                    sb.append(' ');
                    sb.append(Type.getObjectType(methodNode.exceptions.get(i)).getClassName());
                    if (i != exceptionSize - 1) {
                        sb.append(',');
                    }
                }
            }

        }
        return sb.toString();
    }
}
