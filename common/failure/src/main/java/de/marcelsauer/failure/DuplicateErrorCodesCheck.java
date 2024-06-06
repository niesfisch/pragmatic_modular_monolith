package de.marcelsauer.failure;

// @Component
public class DuplicateErrorCodesCheck {

  //  @PostConstruct
  //  public void checkDuplicateErrorCodes() {
  //    Reflections reflections = new Reflections("de.otto");
  //    Set<Class<? extends ErrorCode>> errorCodeClasses =
  // reflections.getSubTypesOf(ErrorCode.class);
  //
  //    Set<String> errorCodes = new HashSet<>();
  //    Set<String> duplicateCodes = new HashSet<>();
  //    for (Class<? extends ErrorCode> errorCodeClass : errorCodeClasses) {
  //      Object[] errorCodeEnumValues = errorCodeClass.getEnumConstants();
  //      for (Object enumValue : errorCodeEnumValues) {
  //        String errorCode = ((ErrorCode) enumValue).code();
  //        boolean isUnique = errorCodes.add(errorCode);
  //        if (!isUnique) {
  //          duplicateCodes.add(errorCode);
  //        }
  //      }
  //    }
  //    if (!duplicateCodes.isEmpty()) {
  //      throw new IllegalStateException("Error codes in the application are not unique:
  // %s".formatted(duplicateCodes));
  //    }
  //  }
}
