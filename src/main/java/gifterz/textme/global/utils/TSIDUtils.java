package gifterz.textme.global.utils;

import com.github.f4b6a3.tsid.TsidCreator;
import org.springframework.stereotype.Component;

@Component
public class TSIDUtils {
    public static String generateTSID() {
        return TsidCreator.getTsid().toString();
    }


}
