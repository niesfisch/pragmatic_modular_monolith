package de.marcelsauer.encryption;

public interface EncryptionService {
  String encrypt(String plaintextValue);

  String decrypt(String ciphertextValue);
}
