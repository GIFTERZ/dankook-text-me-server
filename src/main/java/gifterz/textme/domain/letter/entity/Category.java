package gifterz.textme.domain.letter.entity;

import java.util.Locale;

public enum Category {
    EXCHANGE,
    SAFETY,
    EDUCATION,
    FACILITIES,
    WELFARE,
    ETC;

    public static Category fromName(String name) {
        return Category.valueOf(name.toUpperCase(Locale.ENGLISH));
    }
}
