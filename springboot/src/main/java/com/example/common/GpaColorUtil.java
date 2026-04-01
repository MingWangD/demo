package com.example.common;

import java.math.BigDecimal;

public class GpaColorUtil {

    private GpaColorUtil() {
    }

    public static String resolveColor(BigDecimal gpa) {
        if (gpa == null) {
            return "RED";
        }
        if (gpa.compareTo(new BigDecimal("1.5")) < 0) {
            return "RED";
        }
        if (gpa.compareTo(new BigDecimal("2.0")) < 0) {
            return "ORANGE";
        }
        if (gpa.compareTo(new BigDecimal("2.5")) < 0) {
            return "YELLOW";
        }
        return "GREEN";
    }
}
