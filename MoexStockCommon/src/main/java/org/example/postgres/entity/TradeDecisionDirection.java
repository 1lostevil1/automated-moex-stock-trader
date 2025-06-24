package org.example.postgres.entity;

public enum TradeDecisionDirection {
    LONG,

    SHORT,

    LONG_HOLD,

    SHORT_HOLD;


    /**
     * Проверяет, является ли направление длинной позицией
     */
    public boolean isLong() {
        return this == LONG || this == LONG_HOLD;
    }

    /**
     * Проверяет, является ли направление короткой позицией
     */
    public boolean isShort() {
        return this == SHORT || this == SHORT_HOLD;
    }


    /**
     * Получает базовое направление (конвертирует HOLD в основное направление)
     */
    public TradeDecisionDirection getBaseDirection() {
        return switch (this) {
            case LONG, LONG_HOLD -> LONG;
            case SHORT, SHORT_HOLD -> SHORT;
        };
    }

    /**
     * Проверяет, нужно ли открывать новую позицию
     */
    public boolean isOpening() {
        return this == LONG || this == SHORT;
    }

    /**
     * Проверяет, является ли направление удержанием позиции
     */
    public boolean isHold() {
        return this == LONG_HOLD || this == SHORT_HOLD;
    }
}
