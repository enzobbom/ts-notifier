package com.javanauta.ts.notifier.application.port.email;

import com.javanauta.ts.notifier.application.command.Notification;

public interface EmailComposer {
    public String compose(Notification notification);
}
