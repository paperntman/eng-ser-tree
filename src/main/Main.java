package main;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Main extends JFrame{

    public static enum peopleFile{
        Anchor("아나운서_대사_단_저"), Dukbae("김덕배_대사_단_저"), Citizen1("시민1_대사_단_저"), Citizen2("시민2_대사_단_저"), Activity("부장_대사_단_저");

        final String value;
        peopleFile(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    static int readingL = 0;
    static JSONArray array;
    static JFrame frame;
    static JPanel panel;

    static ImageIcon image = null;

    public static void setImage(String s){
        if (! new File("./src/assets/"+s+".png").exists()) return;
        image = new ImageIcon(new ImageIcon("./src/assets/"+s+".png")
                .getImage()
                .getScaledInstance(360, 640, Image.SCALE_SMOOTH));
    }

    public static void main(String[] args) throws IOException {

        readingL = 0;
        array = null;
        frame = null;
        panel = null;
        image = null;
        frame = new Main();

        frame.setVisible(false);

        setImage("표지");
        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(image.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        final boolean[] pressed = {false};

        frame.setVisible(true);

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println("keyEvent");
                pressed[0] = true;
            }
        };
        frame.getContentPane().addKeyListener(keyAdapter);
        frame.getContentPane().requestFocus();
        System.out.println("stuck");
        frame.add(panel);

        while(!pressed[0]){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }






        System.out.println("done");
        start();
    }

    public static ImageIcon readImage(String s){
        if (! new File("./src/assets/"+s+".png").exists()) {
            return null;
        }
        return new ImageIcon("./src/assets/"+s+".png");
    }

    public Main() throws IOException {
        setResizable(false);
        setSize(360, 640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void addingTalkBalloon(){

    }

    public static void start() throws IOException {
        array = new JSONArray(new String(
                Files.readAllBytes(Path.of(new File("./src/assets/Document.json").getPath()))
        ));

        for(readingL = 0; !array.isNull(readingL) ; readingL++){
            frame.setVisible(false);
            frame.getContentPane().removeAll();


            JSONObject object = array.getJSONObject(readingL);
            String picture = object.optString("Picture", null);
            if(picture != null) setImage(picture);
            if(image != null)
                panel = new JPanel(){
                    @Override
                    protected void paintComponent(Graphics g) {
                        g.drawImage(image.getImage(), 0, 0, null);
                        setOpaque(false);
                        super.paintComponent(g);
                    }
                };
            else panel = new JPanel();
            panel.setLayout(null);


            JSONArray configurations = object.optJSONArray("Configuration");
            String type = object.getString("Type");
            if(Objects.equals(type, "A")){ //대화일 때
                System.out.println("A");
                JSONObject configuration = configurations.getJSONObject(0);
                String fileName = peopleFile.valueOf(configuration.getString("Saying")).getValue();
                System.out.println(fileName);
                String toSay = configuration.getString("speech");
                System.out.println(toSay);

                JLabel ImageLabel = new JLabel();
                ImageLabel.setIcon(readImage(fileName));
                ImageLabel.setLayout(new BorderLayout());
                JLabel sayLabel = new JLabel("                      "+toSay);
                ImageLabel.setBounds(0, 500, 360, 50);
                panel.add(sayLabel);
                ImageLabel.add(sayLabel);
                panel.add(ImageLabel);
                final boolean[] pressed = {false};

                JLabel label = new JLabel("Hello World!");
                panel.add(label);
                panel.setVisible(true);
                frame.getContentPane().add(panel);
                frame.setVisible(true);

                KeyAdapter keyAdapter = new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        System.out.println("keyEvent");
                        pressed[0] = true;
                    }
                };
                frame.getContentPane().addKeyListener(keyAdapter);
                frame.getContentPane().requestFocus();
                System.out.println("stuck");
                while(!pressed[0]){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                System.out.println("good");
                frame.getContentPane().removeKeyListener(keyAdapter);
            }else if(Objects.equals(type, "B")){ //선택일 때
                AtomicBoolean pressed = new AtomicBoolean(false);
                int[] ys = {90, 163, 236};
                IntStream.range(0, configurations.length()).forEach(value -> {
                    JSONObject jsonObject = configurations.getJSONObject(value);
                    String action = jsonObject.getString("Action");
                    String name = jsonObject.getString("Name");
                    String imageName = jsonObject.optString("Picture", null);
                    int result = jsonObject.optInt("Result", 0);


                    JButton button = new JButton(readImage("설명_검정_단_저"));
                    button.setBorderPainted(false);
                    button.setFocusPainted(false);
                    button.setContentAreaFilled(false);
                    button.setBounds(25, ys[value], 300, 47);
                    button.setLayout(new BorderLayout());
                    JLabel label = new JLabel(name);
                    label.setForeground(Color.WHITE);
                    button.add(label);

                    ActionListener listener = e -> {
                        if(imageName != null) setImage(imageName);
                        readingL = Integer.parseInt(action) - 2;
                        pressed.set(true);
                    };

                    button.addActionListener(listener);

                    panel.add(button);
                });

                //JLabel temp = new JLabel(temperature+"°C");
                //temp.setBounds(300, 25, 25, 25);
                //panel.add(temp);
                panel.setVisible(true);
                frame.getContentPane().add(panel);
                frame.setVisible(true);
                while(!pressed.get()) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }else if(Objects.equals(type, "C")){ //점프일 때
                String action = object.getString("Jump");
                readingL = Integer.parseInt(action) - 2;
            }else if(Objects.equals(type, "D")){ //엔딩일 때
                String name = object.getString("Name");
                JButton button = new JButton(readImage("설명_검정_단_저"));
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                button.setBounds(25, 163, 300, 47);
                button.setLayout(new BorderLayout());
                JLabel label = new JLabel(name);
                label.setForeground(Color.WHITE);
                button.add(label);


                AtomicBoolean pressed = new AtomicBoolean(false);
                ActionListener listener = e -> pressed.set(true);

                button.addActionListener(listener);
                panel.add(button);
                panel.setVisible(true);
                frame.getContentPane().add(panel);
                frame.setVisible(true);
                while(!pressed.get()) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                frame.setVisible(false);
                frame.removeAll();
                main(new String[]{});
            }
        }
    }



    public static void stop(){
        final boolean[] pressed = {false};
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println("keyEvent");
                pressed[0] = true;
            }
        };
        frame.getContentPane().addKeyListener(keyAdapter);
        frame.getContentPane().requestFocus();
        System.out.println("stuck");
        while(!pressed[0]){
        }
    }
}
