package com.luxoft.linkparser.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
public class Link {
    private int id;
    private String url;
    private int depth;
    private int number;
}
