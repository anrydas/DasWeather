package das.tools.weather.service;

import org.springframework.stereotype.Service;

@Service
public class CommonUtilsServiceImpl implements CommonUtilsService {

    @Override
    public int toIntTime(String s) {
        String[] split = s.split(":");
        int h = Integer.parseInt(split[0]);
        int m = Integer.parseInt(split[1]);
        return h*60 + m;
    }

    @Override
    public int toIntColor(String c) {
        return Integer.parseInt(c.substring(1), 16);
    }
}
