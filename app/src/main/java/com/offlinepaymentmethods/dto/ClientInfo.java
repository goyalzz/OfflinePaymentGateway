package com.offlinepaymentmethods.dto;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by aakashsharma on 12/05/17.
 */

public class ClientInfo implements Serializable {

    private UUID transactionId = UUID.randomUUID();

    private Integer id;

    private Float availableAmount;

    private Float payAmount;

    private Integer merchantId;

    private PaymentMethod paymentMethod;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Float availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Float getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Float payAmount) {
        this.payAmount = payAmount;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
