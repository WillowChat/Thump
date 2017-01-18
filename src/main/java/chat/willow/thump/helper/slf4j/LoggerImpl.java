package chat.willow.thump.helper.slf4j;

import chat.willow.thump.helper.LogHelper;

public class LoggerImpl implements Logger {
    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void trace(String format) {
        if (this.isTraceEnabled()) {
            LogHelper.debug(format);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (this.isTraceEnabled()) {
            LogHelper.debug(format, arg);
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (this.isTraceEnabled()) {
            LogHelper.debug(format, arg1, arg2);
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (this.isTraceEnabled()) {
            LogHelper.debug(format, arguments);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (this.isTraceEnabled()) {
            LogHelper.debug(msg, t);
        }
    }

    @Override
    public void debug(String format) {
        if (this.isDebugEnabled()) {
            LogHelper.debug(format);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (this.isDebugEnabled()) {
            LogHelper.debug(format, arg);
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (this.isDebugEnabled()) {
            LogHelper.debug(format, arg1, arg2);
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (this.isDebugEnabled()) {
            LogHelper.debug(format, arguments);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (this.isDebugEnabled()) {
            LogHelper.debug(msg, t);
        }
    }

    @Override
    public void info(String format) {
        if (this.isInfoEnabled()) {
            LogHelper.info(format);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (this.isInfoEnabled()) {
            LogHelper.info(format, arg);
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (this.isInfoEnabled()) {
            LogHelper.info(format, arg1, arg2);
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (this.isInfoEnabled()) {
            LogHelper.info(format, arguments);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (this.isInfoEnabled()) {
            LogHelper.info(msg, t);
        }
    }

    @Override
    public void warn(String format) {
        if (this.isWarnEnabled()) {
            LogHelper.warn(format);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (this.isWarnEnabled()) {
            LogHelper.warn(format, arg);
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (this.isWarnEnabled()) {
            LogHelper.warn(format, arguments);
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (this.isWarnEnabled()) {
            LogHelper.warn(format, arg1, arg2);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (this.isWarnEnabled()) {
            LogHelper.warn(msg, t);
        }
    }

    @Override
    public void error(String format) {
        if (this.isErrorEnabled()) {
            LogHelper.error(format);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (this.isErrorEnabled()) {
            LogHelper.error(format, arg);
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (this.isErrorEnabled()) {
            LogHelper.error(format, arg1, arg2);
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (this.isErrorEnabled()) {
            LogHelper.error(format, arguments);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (this.isErrorEnabled()) {
            LogHelper.error(msg, t);
        }
    }
}
