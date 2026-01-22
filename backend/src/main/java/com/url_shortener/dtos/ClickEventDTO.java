package com.url_shortener.dtos;

import lombok.Data;

import java.time.LocalDate;


@Data
public class ClickEventDTO {

    private Long Count;

    private LocalDate clickDate;



}
