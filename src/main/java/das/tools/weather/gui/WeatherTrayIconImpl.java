package das.tools.weather.gui;

import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

@Slf4j
@Component
public class WeatherTrayIconImpl implements WeatherTrayIcon {
    private static final String DEFAULT_ICON_PATH = "src/main/resources/images/weather-default-01.png";
    @Value("${app.confirm-exit}")
    private final boolean isConfirmExit = true;

    private static final String TOOLTIP_MESSAGE = "%s \uD83D\uDD50 %s - %s\n" +
            "\uD83D\uDD25 %.0f\u2103 (як %.0f\u2103) \uD83C\uDF2B %d\uFF05\n" +
            "\uD83D\uDCA8 %s %.0f (до %.0f) км/г\n" +
            "\u2601 %d\uFF05  \u2614 %.0f mm  \uD83D\uDD3D %.0f mmHg";
    private final WeatherService weatherService;
    private final MenuItem[] infoMenuItems;
    private final PopupMenu popupMenu;
    private TrayIcon trayIcon = null;
    public WeatherTrayIconImpl(WeatherService weatherService) {
        this.weatherService = weatherService;
        this.popupMenu = new PopupMenu();
        this.infoMenuItems = new MenuItem[4];
        initTray();
    }

    @Override
    public void updateControls(ForecastWeatherResponse response) {
        if (response != null) {
            setWeatherIcon(response);
            setWeatherInfo(response);
        } else {
            log.error("[WeatherTrayIconImpl].updateControls: There is an empty response!");
        }
    }

    private void setWeatherInfo(ForecastWeatherResponse response) {
        WeatherCurrent current = response.getCurrent();
        String res = String.format(TOOLTIP_MESSAGE,
                response.getLocation().getName(),
                current.getLastUpdate().split(" ")[1],
                current.getCondition().getText(),

                current.getTemperatureC(),
                current.getFeelsLike(),
                current.getHumidity(),

                current.getWindDirection(),
                current.getWindKmh(),
                current.getGust(),

                current.getCloud(),
                current.getPrecipitation(),
                millibarToMmHg(current.getPressureMb())
                );
        if(log.isDebugEnabled()) log.debug("[WeatherTrayIconImpl].setWeatherTooltip: got tooltip={}", res.replaceAll("\n", " "));
        updateInfoMenuItems(res);
        getPopupMenu();
        trayIcon.setToolTip(res);
    }

    private void updateInfoMenuItems(String message) {
        int i = 0;
        for (String s : message.split("\n")) {
            if (infoMenuItems[i] == null) {
                infoMenuItems[i] = new MenuItem();
            }
            infoMenuItems[i].setLabel(s);
            i++;
        }
    }

    private double millibarToMmHg(float mbar) {
        return mbar * 0.750062;
    }

    private void setWeatherIcon(ForecastWeatherResponse response) {
        trayIcon.setImage(weatherService.getRemoteImage(response.getCurrent().getCondition().getIcon()));
    }

    private void updateWeather() {
        updateControls(weatherService.getForecastWeather());
    }

    private PopupMenu getPopupMenu() {
        popupMenu.removeAll();
        for (MenuItem item : infoMenuItems) {
            if (item != null) {
                popupMenu.add(item);
            }
        }
        popupMenu.addSeparator();
        popupMenu.add(getMenuItem("Update Weather", getUpdateWeatherListener()));
        popupMenu.addSeparator();
        popupMenu.add(getMenuItem("Exit Application", getCloseListener()));
        return popupMenu;
    }

    private void initTray() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage(DEFAULT_ICON_PATH);

            trayIcon = new TrayIcon(image, "Das Weather", getPopupMenu());
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                log.error("Couldn't add icon to System Tray", e);
            }
        } else {
            log.error("System Tray unavailable");
            System.exit(10);
        }
    }

    private MenuItem getMenuItem(String label, ActionListener listener) {
        MenuItem item = new MenuItem();
        item.setLabel(label);
        if (listener != null) {
            item.addActionListener(listener);
        }
        return item;
    }

    private ActionListener getCloseListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if(log.isDebugEnabled()) log.debug("[WeatherTrayIconImpl].getCloseListener: Close Menu Item clicked");
                if (isConfirmExit) {
                    int result = JOptionPane.showConfirmDialog(null, "Are you really want to exit?",
                            "Das Weather",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        };
    }

    private ActionListener getUpdateWeatherListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if(log.isDebugEnabled()) log.debug("[WeatherTrayIconImpl].getUpdateWeatherListener: Update Menu Item clicked");
                updateWeather();
            }
        };
    }
}
