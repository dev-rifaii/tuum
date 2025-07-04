package org.rifaii.tuum.exception;

public class ResourceNotFoundException extends RuntimeException implements CodedValue {
  public ResourceNotFoundException() {}
  public ResourceNotFoundException(String message) {
    super(message);
  }

  @Override
  public String getCode() {
    return "NOT_FOUND";
  }
}
