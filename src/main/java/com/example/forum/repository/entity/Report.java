package com.example.forum.repository.entity;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "report")
@Getter
@Setter
public class Report {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String content;

    @Column(insertable = false, updatable = false)
    private Date createdDate;

    @Column(insertable = false, updatable = true)
    private Date updatedDate;
}

