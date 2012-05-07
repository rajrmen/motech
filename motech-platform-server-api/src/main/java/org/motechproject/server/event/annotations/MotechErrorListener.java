package org.motechproject.server.event.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MotechErrorListener {
	String[] subjects();
    MotechListenerType type() default MotechListenerType.MOTECH_EVENT;
}
