package com.example.forum.controller.form;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReportForm {

    private int id;

    @NotBlank
    private String content;
    private Date createdDate;
    private Date updatedDate;

}
