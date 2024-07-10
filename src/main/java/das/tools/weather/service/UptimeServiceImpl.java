package das.tools.weather.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service @Scope(value = "singleton")
@Slf4j
public class UptimeServiceImpl implements UptimeService {
    private long startTimeMillis;

    @PostConstruct
    private void initStartTime() {
        startTimeMillis = System.currentTimeMillis();
    }

    @Override
    public long getUptimeMillis() {
        return System.currentTimeMillis() - this.startTimeMillis;
    }

    @Override
    public String getFormattedUptime() {
        long seconds = getUptimeMillis() / 1000;
        StringBuilder sb = new StringBuilder();
        long days = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (days * 24);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        if (log.isDebugEnabled()) log.debug("Got seconds={}, days={}, hours={}, minutes={}", seconds, days, hours, minutes);
        if (days > 0) sb.append(String.format("%d days ", days));
        sb.append(String.format("%02d:%02d", hours, minutes));
        if (log.isDebugEnabled()) log.debug("Got uptime={}", sb);
        return sb.toString();
    }
}
