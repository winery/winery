/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.winery;

/**
 * Separate class for logging.
 *
 * We need to add a wrapper around it as developers won't write Logger.debug(this, "msg: %[exception]s", e) for every logged exception
 */
public class Logger {

	/** begin: direct pass-through **/

	public static void trace(final Object source, final String msg) {
		com.jcabi.log.Logger.trace(source, msg);
	}

	public static void trace(
			final Object source,
			final String msg, final Object... args
	) {
		com.jcabi.log.Logger.trace(source, msg, args);
	}

	public static void debug(final Object source, final String msg) {
		com.jcabi.log.Logger.debug(source, msg);
	}

	public static void debug(
			final Object source,
			final String msg, final Object... args
	) {
		com.jcabi.log.Logger.debug(source, msg, args);
	}

	public static void info(final Object source, final String msg) {
		com.jcabi.log.Logger.info(source, msg);
	}

	public static void info(
			final Object source,
			final String msg, final Object... args
	) {
		com.jcabi.log.Logger.info(source, msg, args);
	}

	public static void warn(final Object source, final String msg) {
		com.jcabi.log.Logger.warn(source, msg);
	}

	public static void warn(
			final Object source,
			final String msg, final Object... args
	) {
		com.jcabi.log.Logger.warn(source, msg, args);
	}

	public static void error(final Object source, final String msg) {
		com.jcabi.log.Logger.error(source, msg);
	}

	public static void error(final Object source,
							 final String msg, final Object... args) {
		com.jcabi.log.Logger.error(source, msg, args);
	}

	/** end of direct pass through **/

	public static void trace(final Object source, final String msg, Throwable e) {
		com.jcabi.log.Logger.trace(source, msg);
	}

	public static void trace(
			final Object source,
			final String msg, final Throwable e, final Object... args
	) {
		if (com.jcabi.log.Logger.isTraceEnabled(source)) {
			com.jcabi.log.Logger.trace(source, msg + ": %[exception]s", merge(args, e));
		}
	}

	public static void debug(final Object source, final Throwable e, final String msg) {
		com.jcabi.log.Logger.debug(source, msg);
	}

	public static void debug(
			final Object source,
			final String msg, final Throwable e, final Object... args
	) {
		if (com.jcabi.log.Logger.isDebugEnabled(source)) {
			com.jcabi.log.Logger.debug(source, msg + ": %[exception]s", merge(args, e));
		}
	}

	public static void info(final Object source, final Throwable e, final String msg) {
		com.jcabi.log.Logger.info(source, msg);
	}

	public static void info(final Object source, final Throwable e) {
		com.jcabi.log.Logger.info(source, "%[exception]s", e);
	}

	public static void info(
			final Object source,
			final String msg, final Throwable e, final Object... args
	) {
		if (com.jcabi.log.Logger.isInfoEnabled(source)) {
			com.jcabi.log.Logger.info(source, msg + ": %[exception]s", merge(args, e));
		}
	}

	public static void warn(final Object source, final Throwable e, final String msg) {
		com.jcabi.log.Logger.warn(source, msg);
	}

	public static void warn(final Object source, final Throwable e) {
		com.jcabi.log.Logger.warn(source, "%[exception]s", e);
	}

	public static void warn(
			final Object source,
			final String msg, final Throwable e, final Object... args
	) {
		if (com.jcabi.log.Logger.isWarnEnabled(source)) {
			com.jcabi.log.Logger.warn(source, msg + ": %[exception]s", merge(args, e));
		}
	}

	public static void error(final Object source, final Throwable e, final String msg) {
		com.jcabi.log.Logger.error(source, msg);
	}

	public static void error(final Object source,
							 final String msg, final Throwable e, final Object... args) {
		// error is always logged. Therefore, no isErrorEnabled(Object) exists
		com.jcabi.log.Logger.error(source, msg + ": %[exception]s", merge(args, e));
	}

	/**
	 * Merges args and e together into an array starting with args
	 */
	private static Object[] merge(Object[] args, Throwable e) {
		final int length = args.length;
		if (length == 0) {
			return new Object[]{e};
		} else {
			Object[] res = new Object[length + 1];
			System.arraycopy(args, 0, res, 0, length);
			res[length - 1] = e;
			return res;
		}
	}
}
