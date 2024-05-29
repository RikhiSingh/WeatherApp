import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {

    private JSONObject weatherData;

    public WeatherAppGUI(){
        // setup tittle and gui
        super("Weather App | RIKHI");

        // configure gui tio end program processes on closing
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // size of gui
        setSize(460, 650);

        // load gui in center
        setLocationRelativeTo(null);

        // layout manager null; manually position components
        setLayout(null);

        // prevent any resize of app
        setResizable(false);

        addGUIComponents();
    }

    private void addGUIComponents(){
        // search field
        JTextField searchTextField = new JTextField();

        // set location and size of text field
        searchTextField.setBounds(15,15,351,45);

        // font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24)  );

        add(searchTextField);

        // weather Image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        // temperature
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //center text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // weather description
        JLabel weatherConditionDescription = new JLabel("Cloudy");
        weatherConditionDescription.setBounds(0,405,450,36);
        weatherConditionDescription.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDescription.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDescription);

        // humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        // humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // wind speed image
        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImage.setBounds(200,500,74,66);
        add(windSpeedImage);

        // wind speed text
        JLabel windSpeedText = new JLabel("<html><b>Wind Speed</b> 15km/h</html>");
        windSpeedText.setBounds(290,500,94,55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);


        // search btn
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        // change cursor to hand
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // get lcoation from the user
              String userInput = searchTextField.getText();

              // validate input
                if(userInput.replaceAll("\\s","").length() <= 0){
                    return;
                }

                // retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);


                // update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                // update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                // update weather condition text
                weatherConditionDescription.setText(weatherCondition);

                // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // update wind speed
                double windSpeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Wind Speed</b> " + windSpeed + "km/h</html>");
            }
        });
        add(searchButton);

    }

    // to create images in GUI Components
    private ImageIcon loadImage(String resourcePath){
        try{
            // read image file form path
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // return image icon for component to render
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}
