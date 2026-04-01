package com.example.algorithm;

import com.example.entity.StudentFeature;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LogisticRegression {

    private final double bias = 0.35;
    private final double wAttendanceRate = -2.2;
    private final double wHomeworkSubmitRate = -1.8;
    private final double wHomeworkAvg = -0.018;
    private final double wExamAvg = -0.020;
    private final double wGpa = -1.4;
    private final double wMissingHomework = 0.65;
    private final double wAbsentExam = 0.95;

    public double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }

    public BigDecimal predictProbability(StudentFeature feature) {
        double z = bias
                + wAttendanceRate * n(feature.getAttendanceRate())
                + wHomeworkSubmitRate * n(feature.getHomeworkSubmitRate())
                + wHomeworkAvg * n(feature.getHomeworkAvgScore())
                + wExamAvg * n(feature.getExamAvgScore())
                + wGpa * n(feature.getGpa())
                + wMissingHomework * n(feature.getMissingHomeworkCount())
                + wAbsentExam * n(feature.getAbsentExamCount());
        return BigDecimal.valueOf(sigmoid(z));
    }

    private double n(BigDecimal val) {
        return val == null ? 0D : val.doubleValue();
    }

    private double n(Integer val) {
        return val == null ? 0D : val;
    }
}
