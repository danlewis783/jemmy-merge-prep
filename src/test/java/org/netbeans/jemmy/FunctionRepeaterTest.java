package org.netbeans.jemmy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertSame;
class FunctionRepeaterTest {
    private static final Logger logger = LoggerFactory.getLogger(FunctionRepeaterTest.class);

    @Test
    void testIssue30537() throws Exception {
        Object obj = new Object();
        assertSame(obj, FunctionRepeater.on(f -> f).runUntilNotNull(obj));
    }
}
