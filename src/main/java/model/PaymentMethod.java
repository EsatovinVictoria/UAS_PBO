package model;

public enum PaymentMethod {
  CASH("Cash"), EWALLET("E-Wallet"), DEBIT("Debit Card"), CREDIT("Credit Card"), QRIS("QRIS"), BANK_TRANSFER(
      "Bank Transfer");

  private final String label;

  PaymentMethod(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return label;
  }

  public static PaymentMethod fromString(String text) {
    if (text == null || text.trim().isEmpty()) {
      return CASH;
    }

    for (PaymentMethod method : PaymentMethod.values()) {
      if (method.label.equalsIgnoreCase(text.trim())) {
        return method;
      }
    }

    // Default fallback
    return CASH;
  }
}