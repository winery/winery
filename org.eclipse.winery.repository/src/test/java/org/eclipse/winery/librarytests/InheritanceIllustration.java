/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.librarytests;
/**
 * This class is intended to demonstrate static resolution of overloaded methods
 *
 * The output of this class is "Doing sth. with a followed by "Doing sth. with
 * b" even if the passed "theObject" is of type B
 */
public class InheritanceIllustration {

	private static class A {
	}

	private static class B extends A {
	}

	private static class X {

		public static void doSomething(A a) {
			System.out.println("Doing sth. with a");
		}

		public static void doSomething(B b) {
			System.out.println("Doing sth. with b");
		}
	}


	public static void main(String[] args) {
		A theObject = new B();
		X.doSomething(theObject);
		X.doSomething((B) theObject);
	}

}
