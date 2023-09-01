package service.chat.mealmate.mealmate.dto;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumMappingStrategy {
    private static String getValidList(Enum<?>[] requestEnums) {
        return Arrays.stream(requestEnums)
                .map(i -> i.name())
                .collect(Collectors.joining(", "));
    };

    public static Enum<?> validate(Enum<?>[] requestEnums, String target) {
        return Arrays.stream(requestEnums)
                .filter(i -> i.name().equals(target.replaceAll("[^a-zA-Z]", "").toUpperCase()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("매핑 실패. 유요한 값들은 다음과 같습니다: " + getValidList(requestEnums)));
    }
}
