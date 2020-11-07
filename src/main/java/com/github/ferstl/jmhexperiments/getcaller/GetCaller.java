package com.github.ferstl.jmhexperiments.getcaller;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.lang.invoke.MethodHandles;
import java.util.EnumSet;
import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public class GetCaller {

  private static final StackWalker STACK_WALKER_FOR_CLASS = StackWalker.getInstance(EnumSet.of(RETAIN_CLASS_REFERENCE), 2);

  private static final StackWalker STACK_WALKER_FOR_CLASS_NAME = StackWalker.getInstance(EnumSet.noneOf(Option.class), 2);

  public static Class<?> getCallerClassByLookup() {
    return MethodHandles.lookup().lookupClass();
  }

  public static Class<?> getCallerClassByStackWalker() {
    return STACK_WALKER_FOR_CLASS.walk(s -> s.skip(1)
        .map(StackFrame::getDeclaringClass)
        .findFirst()
        .get());
  }

  public static String getCallerClassNameByStackWalker() {
    return STACK_WALKER_FOR_CLASS_NAME.walk(s -> s.skip(1)
        .map(StackFrame::getClassName)
        .findFirst()
        .get());
  }

  public static String getCallerClassNameByException() {
    return new RuntimeException().getStackTrace()[1].getClassName();
  }

  // public static Class<?> getCallerClassByReflection() {
  // return sun.reflect.Reflection.getCallerClass();
  // return jdk.internal.reflect.Reflection.getCallerClass()();
  // }

}
