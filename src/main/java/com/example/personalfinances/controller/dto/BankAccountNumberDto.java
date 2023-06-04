package com.example.personalfinances.controller.dto;

import lombok.Builder;
import lombok.Data;
//TODO
@Builder
@Data
public class BankAccountNumberDto {
private String banAlias;
private String banNumber;
}
