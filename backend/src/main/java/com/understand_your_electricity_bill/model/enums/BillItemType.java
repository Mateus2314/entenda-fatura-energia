package com.understand_your_electricity_bill.model.enums;

public enum BillItemType {
    // Consumo por horário
    OFF_PEAK_CONSUMPTION,      // Fora Ponta
    PEAK_CONSUMPTION,          // Ponta
    INTERMEDIATE_CONSUMPTION,  // Intermediário

    // Tarifas
    TUSD_CHARGE,
    TE_CHARGE,

    // Bandeiras
    FLAG_CHARGE,

    // Tributos
    ICMS_TAX,
    PIS_TAX,
    COFINS_TAX,

    // Outros
    PUBLIC_LIGHTING,           // Contribuição Iluminação Pública
    DISCOUNT,
    CREDIT,
    OTHER
}
