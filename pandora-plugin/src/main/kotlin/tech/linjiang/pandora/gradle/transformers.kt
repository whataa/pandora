package tech.linjiang.pandora.gradle

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

private const val CLAZZ_NAME = "okhttp3/OkHttpClient\$Builder"
private const val METHOD_NAME = "<init>"
private const val FIELD_NAME = "interceptors"

internal class OkHttpTransform : Transformer {

    override fun transform(clazz: ClassNode) {
        clazz.takeIf { it.name == CLAZZ_NAME }?.methods
                ?.find { it.name == METHOD_NAME }?.instructions
                ?.let { instructions ->
                    Iterable { instructions.iterator() }
                            .filterIsInstance<FieldInsnNode>()
                            .find { it.name == FIELD_NAME }
                            ?.let { src ->
                                println("AOP: ${src.owner}, ${src.name}, ${src.desc}")
                                fun AbstractInsnNode.append(node: AbstractInsnNode) = node.also {
                                    instructions.insert(this, it)
                                }
                                // interceptors.add(tech.linjiang.pandora.Pandora.get().getInterceptor())
                                src.append(VarInsnNode(ALOAD, 0))
                                        .append(FieldInsnNode(GETFIELD, src.owner, src.name, src.desc))
                                        .append(MethodInsnNode(INVOKESTATIC, "tech/linjiang/pandora/Pandora", "get", "()Ltech/linjiang/pandora/Pandora;", false))
                                        .append(MethodInsnNode(INVOKEVIRTUAL, "tech/linjiang/pandora/Pandora", "getInterceptor", "()Ltech/linjiang/pandora/network/OkHttpInterceptor;", false))
                                        .append(MethodInsnNode(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true))
                                        .append(InsnNode(POP))
                            }
                }
    }

}

