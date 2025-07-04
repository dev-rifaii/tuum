package org.rifaii.tuum.audit;

public record DataChangeLog(
    String operationType,
    String executedMethodName,
    String parameter
) {}
