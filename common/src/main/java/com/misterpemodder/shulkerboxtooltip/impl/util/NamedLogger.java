package com.misterpemodder.shulkerboxtooltip.impl.util;

import org.apache.logging.log4j.Logger;

/**
 * A {@link Logger} wrapper that always prints the name of the logger regardless of configuration.
 */
public final class NamedLogger {
  private final Logger inner;

  public NamedLogger(Logger inner) {
    this.inner = inner;
  }

  public void error(String message) {
    this.inner.error('[' + inner.getName() + "] " + message);
  }

  public void error(String message, Exception error) {
    this.inner.error('[' + inner.getName() + "] " + message, error);
  }

  public void debug(String message) {
    this.inner.debug('[' + inner.getName() + "] " + message);
  }

  public void info(String message) {
    this.inner.info('[' + inner.getName() + "] " + message);
  }

  public void info(String message, Object arg1) {
    this.inner.info('[' + inner.getName() + "] " + message, arg1);
  }

  public void info(String message, Object arg1, Object arg2) {
    this.inner.info('[' + inner.getName() + "] " + message, arg1, arg2);
  }

  public void warn(String message) {
    this.inner.warn('[' + inner.getName() + "] " + message);
  }
}
