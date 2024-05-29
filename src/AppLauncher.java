import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // display weather app gui
                new WeatherAppGUI().setVisible(true);

                // data returned by
                // WeatherAPPSystem.out.println(WeatherApp.getLocationData("Tokyo"));

                System.out.println(WeatherApp.getCurrentTime());
            }
        });
    }
}
