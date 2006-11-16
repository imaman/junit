package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.internal.javamodel.JavaClass;
import org.junit.internal.javamodel.JavaMethod;
import org.junit.runner.Runner;

public class JavaTestInterpreter {

	public JavaTestInterpreter() {

	}

	// TODO: push out
	// TODO: be suspicious of everywhere this is constructed

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.internal.runners.IJavaTestInterpreter#executeMethodBody(java.lang.Object,
	 *      org.junit.internal.runners.JavaMethod)
	 */
	public void executeMethodBody(Object test, JavaMethod javaMethod)
			throws IllegalAccessException, InvocationTargetException {
		javaMethod.invoke(test);
	}

	public Runner buildRunner(Class<?> klass) throws InitializationError {
		JavaClass built= buildClass(klass);
		MethodValidator methodValidator= new MethodValidator(built);
		validate(methodValidator);
		methodValidator.assertValid();
		return new TestClassMethodsRunner(built, this);
	}

	protected void validate(MethodValidator methodValidator) {
		methodValidator.validateAllMethods();
	}
	
	public JavaMethod interpretJavaMethod(final JavaClass klass, Method method) {
		return new JavaMethod(klass, method);
	}

	public JavaClass buildClass(Class<?> superclass) {
		return new JavaClass(superclass, this);
	}
}
