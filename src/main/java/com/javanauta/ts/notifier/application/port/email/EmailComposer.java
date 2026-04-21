package com.javanauta.ts.notifier.application.port.email;

import com.javanauta.ts.notifier.application.command.NotifyTaskCommand;

public interface EmailComposer {
    public String compose(NotifyTaskCommand notifyTaskCommand);
}
