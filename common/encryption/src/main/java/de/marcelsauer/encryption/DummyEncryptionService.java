package de.marcelsauer.encryption;

import org.springframework.stereotype.Component;

@Component
public class DummyEncryptionService implements EncryptionService {
  private static final String PREFIX = "encrypted_";

  @Override
  public String encrypt(String plainText) {
    return PREFIX + plainText;
  }

  @Override
  public String decrypt(String ciphertextValue) {
    return ciphertextValue.substring(PREFIX.length());
  }
}
