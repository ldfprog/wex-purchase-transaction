package org.lucasdf.models;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
public class WexTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 50, unique = true)
    private String description;

    private LocalDate date;

    private Float amount;

    public WexTransaction(String description, LocalDate date, Float amount) {
        this.description = description;
        this.date = date;
        this.amount = amount;
    }

    public WexTransaction() {}

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public Float getAmount() {
        return amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
