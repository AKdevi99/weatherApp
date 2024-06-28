import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class weatherappgui extends  JFrame {
    private JSONObject weatherData;

    public weatherappgui() {
        setTitle("Weather App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600);
        getContentPane().setLayout(null);
        getContentPane().setBackground(new Color(254,250,224));
        setLocationRelativeTo(null);
        setResizable(false);
        addcomponents();
        setVisible(true);

    }

    public void addcomponents() {
        //search field
        JTextField search = new JTextField();
        search.setText("Welcome to Weather App");
        search.setBounds(15, 15, 351, 45);
        search.setFont(new Font("ROBOTO", Font.BOLD, 20));
        search.setBorder(new CurvedBorder());
        search.setOpaque(false);
        search.setHorizontalAlignment(SwingUtilities.CENTER);
        getContentPane().add(search);






        //add weathercondition image
        JLabel weathercondition  = new JLabel(loadimage("src/assets/sunny.png"));
        weathercondition.setBounds(0,90,450,245);
        add(weathercondition);

        //adding temperature
        JLabel temperature = new JLabel("<html><b>10&deg;C</b></html>");
        temperature.setBounds(0,350,450,54);
        temperature.setFont(new Font("Dialog", Font.PLAIN, 30));
        temperature.setHorizontalAlignment(SwingUtilities.CENTER);
        add(temperature);

        //adding a temperature text
        JLabel temperaturetext = new JLabel("<html><b>It's a sunny day! </b></html>");
        temperaturetext.setBounds(10,380,450,54);
        temperaturetext.setFont(new Font("Dialog", Font.PLAIN, 25));
        temperaturetext.setHorizontalAlignment(SwingUtilities.CENTER);
        add(temperaturetext);

        //adding humidity image
        JLabel humidity = new JLabel(loadimage("src/assets/humidity.png"));
        humidity.setBounds(25,460,74,70);
        add(humidity);

        //adding humidity text
        JLabel humiditytext = new JLabel("<html><b>Humidity:</b><br><p>89%</p></html>");
        humiditytext.setBounds(100,460,74,70);
        add(humiditytext);


        //adding windspeed image
        JLabel windspeedimage = new JLabel(loadimage("src/assets/windspeed.png"));
        windspeedimage.setBounds(250,460,74,70);
        add(windspeedimage);

        //adding windspeed text
        JLabel windspeedtext = new JLabel("<html><b>WindSpeed:</b><br><p>15Km/hr</p></html>");
        windspeedtext.setBounds(335,460,74,70);
        add(windspeedtext);

        //add search button
        JButton searchbutton = new JButton(loadimage("src/assets/disaster2.png"));
        searchbutton.setBounds(385,13,40,45);
        searchbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userInput = search.getText();
                if(userInput.replaceAll("\\s","").length()<=0) {
                    return;
                }
                //get the weather data from weather api
                weatherData = weatherapp.getWeatherData(userInput);

                //update the weather image
                String weatherCondition = weatherData.get("weatherCondition").toString();

                switch(weatherCondition) {
                    case "sunny":
                        weathercondition.setIcon(loadimage("src/assets/sunny.png"));
                        temperaturetext.setText("It's a sunny day!");
                        break;
                        case "Rainy":
                            weathercondition.setIcon(loadimage("src/assets/rain.png"));
                            temperaturetext.setText("It's rainy day!");
                            break;
                            case "Cloudy":
                                weathercondition.setIcon(loadimage("src/assets/cloudy.png"));
                                temperaturetext.setText("It's cloudy day!");
                                break;
                    case "Snow" :
                        weathercondition.setIcon(loadimage("src/assets/snow.png"));
                        temperaturetext.setText("It's snow day!");
                        break;
                }

                //update temperature condtion text
                double Temperature = (double) weatherData.get("temperature");
                temperature.setText("<html><b>"+Temperature+"&deg;C</b></html>");


                //update humidity
                long HUMIDITY = (long) weatherData.get("humidity");
                humiditytext.setText("<html><b>Humidity:</b><br><p>"+HUMIDITY+"%</p></html>");

                //update windspeed
                double WINDSPEED = (double) weatherData.get("windspeed");
                windspeedtext.setText("<html><b>WindSpeed:</b><br><p>"+WINDSPEED+"Km/hr</p></html>");


            }
        });
        add(searchbutton);




    }


    //extra methods
    class CurvedBorder extends AbstractBorder {
        private final int radius = 40;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.GRAY);  // Border color
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = 4;
            return insets;
        }

    }

    private ImageIcon loadimage(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));

            return new ImageIcon(image);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
