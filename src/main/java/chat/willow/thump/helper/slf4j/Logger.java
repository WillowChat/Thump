package chat.willow.thump.helper.slf4j;

// NOTE: Modified for use as a shim in this mod

/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 * <p/>
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * <p/>
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * <p/>
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

public interface Logger {
    boolean isTraceEnabled();

    boolean isDebugEnabled();

    boolean isInfoEnabled();

    boolean isWarnEnabled();

    boolean isErrorEnabled();

    void trace(String msg);

    void trace(String format, Object arg);

    void trace(String format, Object arg1, Object arg2);

    void trace(String format, Object... arguments);

    void trace(String msg, Throwable t);

    void debug(String msg);

    void debug(String format, Object arg);

    void debug(String format, Object arg1, Object arg2);

    void debug(String format, Object... arguments);

    void debug(String msg, Throwable t);

    void info(String msg);

    void info(String format, Object arg);

    void info(String format, Object arg1, Object arg2);

    void info(String format, Object... arguments);

    void info(String msg, Throwable t);

    void warn(String msg);

    void warn(String format, Object arg);

    void warn(String format, Object... arguments);

    void warn(String format, Object arg1, Object arg2);

    void warn(String msg, Throwable t);

    void error(String msg);

    void error(String format, Object arg);

    void error(String format, Object arg1, Object arg2);

    void error(String format, Object... arguments);

    void error(String msg, Throwable t);
}