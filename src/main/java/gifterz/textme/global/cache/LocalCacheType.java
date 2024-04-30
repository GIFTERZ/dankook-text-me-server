package gifterz.textme.global.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;

@Getter
@RequiredArgsConstructor
public enum LocalCacheType {
    CARD("card", 1, DAYS, 100);

    private final String name;
    private final int expireTime;
    private final TimeUnit timeUnit;
    private final int maximumSize;
}
