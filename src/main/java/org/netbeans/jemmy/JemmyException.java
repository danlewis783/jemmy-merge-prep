package org.netbeans.jemmy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.io.PrintWriter;


public class JemmyException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(JemmyException.class);
    private Object object = null;

    public JemmyException(String description) {
        super(description);
    }

    public JemmyException(String description, Object object) {
        this(description);
        this.object = object;
    }

    public JemmyException(String description, Throwable cause) {
        super(description, cause);
    }

    public JemmyException(String description, Throwable cause, Object object) {
        super(description, cause);
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public void printStackTrace() {
        logger.warn("", this);
    }

    @Override
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);

        if (object != null) {
            ps.println("Object:");
            ps.println(object.toString());
        }
    }

    @Override
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);

        if (object != null) {
            pw.println("Object:");
            pw.println(object.toString());
        }
    }
}
