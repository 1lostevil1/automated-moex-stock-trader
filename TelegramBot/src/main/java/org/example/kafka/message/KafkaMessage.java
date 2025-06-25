package org.example.kafka.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KafkaMessage {
    private String ticker;
    private double price;
    private int amount;
    private String direction;

    @JsonProperty("ticker")
    public String getTicker() {
        return ticker;
    }
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
    @JsonProperty("price")
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    @JsonProperty("amount")
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    @JsonProperty("direction")
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
