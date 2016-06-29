/*
 * StackTracer.java:  utility methods to trace stack
 *
 * Copyright (c) 2015 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nomencurator.util;

import java.io.PrintStream;

/**
 * StackTracer provieds a collection of static utility methods to help tracing stack.
 */
public class StackTracer {
    private StackTracer() {}

    /**
     * Returns an array of stack trace infromation under execution.
     */
    public static StackTraceElement[] getStackTrace() {
	return new Throwable().getStackTrace();
    }

    /**
     * Returns an array of stack trace infromation under execution.
     */
    public static StackTraceElement getStackTrace(int depth) {
	return new Throwable().getStackTrace()[depth + 1];
    }


    /**
     * Returns an array of stack trace information of the source code line under execution.
     */
    public static StackTraceElement getCurrentTrace() {
	return getCurrentTrace(0);
    }
    /**
     * Returns an array of stack trace information of the source code line under execution.
     */
    public static StackTraceElement getCurrentTrace(int depth) {
	return getStackTrace(depth + 1);
    }


    /**
     * Returns an array of stack trace information of the source code colled the line under execution.
     */
    public static StackTraceElement getCallerTrace() {
	return getCallerTrace(0);
    }

    /**
     * Returns an array of stack trace information of the source code colled the line under execution.
     */
    public static StackTraceElement getCallerTrace(int depth) {
	return getStackTrace(depth + 2);
    }

    /**
     * Prints a  stack trace information line to <tt>stream</tt>.
     *
     * @param stacTrace to print
     * @param stream to print to
     */
    public static void print(StackTraceElement stackTrace, PrintStream stream) {
	print(stackTrace, stream, null);
    }

    /**
     * Prints a stack trace information line to <tt>stream</tt> with optional <tt>message</tt>
     *
     * @param stacTrace to print
     * @param stream to print to
     * @param message to append
     */
    public static void print(StackTraceElement stackTrace, PrintStream stream, String message) {
	stream.println(toString(stackTrace, message));
    }

    /**
     * Returns a String representing the stack trace information with optional <tt>message</tt>
     *
     * @param stacTrace to print
     * @param message to append
     */
    public static String toString(StackTraceElement stackTrace, String message) {
	StringBuffer buffer = new StringBuffer().append("at ").append(stackTrace.getClassName()).append("#").append(stackTrace.getMethodName());
	buffer.append("(").append(stackTrace.getFileName()).append(":");
	if(stackTrace.isNativeMethod()) 
	    buffer.append("native method");
	else
	    buffer.append(stackTrace.getLineNumber());
	buffer.append(")");
	if(message != null)
	    buffer.append(" ").append(message);

	return buffer.toString();
    }

    /**
     * Returns a String representing the stack trace information under excecution with optional <tt>message</tt>
     *
     * @param message to append
     */
    public static String currentTraceString(String message, int depth) {
	return toString(getCurrentTrace(depth + 1), message);
    }


    /**
     * Returns a String representing the stack trace information under excecution with optional <tt>message</tt>
     *
     * @param message to append
     */
    public static String currentTraceString(String message) {
	return currentTraceString(message, 1);
    }

    /**
     * Returns a String representing the stack trace information of the method called the line under excecution with optional <tt>message</tt>
     *
     * @param message to append
     */
    public static String callerTraceString(String message, int depth) {
	return toString(getCallerTrace(depth + 1), message);
    }

    /**
     * Returns a String representing the stack trace information of the method called the line under excecution with optional <tt>message</tt>
     *
     * @param message to append
     */
    public static String callerTraceString(String message) {
	return callerTraceString(message, 1);
    }

    /**
     * Prints a stack trace information of the line under exectuion to <tt>stream</tt> with optional <tt>message</tt>
     *
     * @param stream to print to
     * @param message to append
     */
    public static void printCurrent(PrintStream stream, String message, int depth) {
	stream.println(currentTraceString(message, depth + 1));
    }

    /**
     * Prints a stack trace information of the line under exectuion to <tt>stream</tt> with optional <tt>message</tt>
     *
     * @param stream to print to
     * @param message to append
     */
    public static void printCurrent(PrintStream stream, String message) {
	printCurrent(stream, message, 1);
    }

    /**
     * Prints a stack trace information of the method called the line under exectuion to <tt>stream</tt> with optional <tt>message</tt>
     *
     * @param stream to print to
     * @param message to append
     */
    public static void printCaller(PrintStream stream, String message, int depth) {
	stream.println(callerTraceString(message, depth + 1));
    }

    /**
     * Prints a stack trace information of the method called the line under exectuion to <tt>stream</tt> with optional <tt>message</tt>
     *
     * @param stream to print to
     * @param message to append
     */
    public static void printCaller(PrintStream stream, String message) {
	printCaller(stream, message, 1);
    }
}
