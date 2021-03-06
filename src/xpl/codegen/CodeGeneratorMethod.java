package xpl.codegen;

import xpl.semantic.ast.*;
import xpl.semantic.symbols.*;
import xpl.semantic.Types;

import java.util.*;

import org.objectweb.asm.MethodVisitor;

public class CodeGeneratorMethod extends CodeGeneratorModule {
  private Stack<MethodVisitor> methods = new Stack<MethodVisitor>();

  public CodeGeneratorMethod(Context context) {
    super(context);
  }

  public void definition(MethodNode definition) {
    if(currentMethod != null)
      methods.push(currentMethod);

    Method method = definition.getMethod();
    String name = method.getName();
    MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, name, method.getSignature(), null, null);
    context.switchMethodVisitor(methodVisitor);
  }

  public void ret() {
    currentMethod.visitInsn(IRETURN);
  }

  public void finish(MethodNode definition) {
    Method method = definition.getMethod();
    currentMethod.visitInsn(InstructionSet.RETURN(method.getReturnType()));
    currentMethod.visitMaxs(10, method.getLocalsSize());
    context.switchMethodVisitor(methods.pop());
  }

  public void prepareCall(MethodNode call) {
    Method method = call.getMethod();

    if(!method.isBuiltin())
      currentMethod.visitVarInsn(ALOAD, 0);
  }

  public void call(MethodNode call) {
    Method method         = call.getMethod();
    String name           = method.getName();
    int    invocationKind = method.isBuiltin() ? INVOKESTATIC : INVOKEVIRTUAL;
    String invokingClass  = method.isBuiltin() ? "xpl/runtime/Runtime" : className;

    currentMethod.visitMethodInsn(invocationKind, invokingClass, name, method.getSignature());
  }
}
