package com.artinus.core.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "mem_mst")
@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
