/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.librarytests;

/**
 * This class is intended to demonstrate static resolution of overloaded methods
 * <p>
 * The output of this class is "Doing sth. with a followed by "Doing sth. with b" even if the passed "theObject" is of
 * type B
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
