package personal.wmware.exam.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import personal.wmware.exam.common.config.CommonConfig;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Utils {
    private final CommonConfig config;

    public String getCatalogIndex(String catalogName) {
        return String.format("%s%s", config.getCatalogPrefix(), catalogName);
    }

}
