package com.javanauta.ts.notifier.service.ports.email;

import com.javanauta.ts.notifier.service.model.Notification;

public interface EmailComposer {
    public String compose(Notification notification);
}
