package com.javanauta.ts.notifier.domain.ports.email;

public interface EmailComposer {
    public String composeEmail(String taskName, String taskScheduledTime, String taskDescription);
}
