package com.github.kozakatv.TwitchChatClient;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Ignacio Alvarez someoneigna@gmail.com
 * @class com.github.kozakatv.TwitchChatReader.GUIConsole
 * A JFrame showing console output
 */
@Slf4j
final public class GUIConsole {

    private static JTextArea console;
    private static JFrame frame;
    private static JScrollPane scrollPane;
    private static PrintStream outStream;

    public static void run(final String title, final int width, final int height, String pathToFileOnDisk) {
        console = new JTextArea("", 10, 30);
        DefaultCaret caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        frame = new JFrame(title);
        frame.setSize(width, height);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        console.setEditable(false);
        console.setLineWrap(true);

        //Default JTextFrame font colors
        console.setBackground(Color.BLACK);
        console.setForeground(Color.WHITE);
        console.setFont(new Font("Sans Serif", Font.PLAIN, 20));

        scrollPane = new JScrollPane(console);
        //scrollPane.setBorder(new EmptyBorder(8, 8, 8, 8));
        scrollPane.setBorder(BorderFactory.createMatteBorder(4, 10, 4, 4, Color.BLACK));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        frame.getContentPane().setLayout(new GridLayout());
        frame.getContentPane().add(scrollPane);
        frame.setResizable(true);
        frame.setLocation(200, 100);


        //Image icon = Toolkit.getDefaultToolkit().getImage(pathToFileOnDisk);
        // frame.setIconImage(icon);
        frame.setIconImage(new ImageIcon("logo.gif").getImage());


        //Listeners
        console.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {

                    case KeyEvent.VK_F9:
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                Color foreground = JColorChooser.showDialog(new JPanel(), "Choose Foreground color", Color.BLACK);
                                console.setForeground(foreground);
                            }

                        });
                        break;


                    case KeyEvent.VK_F10:

                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                Color background = JColorChooser.showDialog(new JPanel(), "Choose Background color", Color.BLACK);
                                console.setBackground(background);
                            }

                        });
                        break;

                    default:
                        break;
                }//End of switch(KeyEvent e)

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }


        });

        //outStream = new PrintStream(new OutputStream() {

        //    @Override
        //    public void write ( int b)throws IOException {
        //com.github.kozakatv.TwitchChatReader.GUIConsole.append(new String(Character.toChars(b)));
        //    }
        //}
        //);

        System.setOut(outStream);

        frame.pack();
        scrollPane.setVisible(true);
        frame.setVisible(true);
    }

    public static void setText(String text) {
        console.setText(text);
    }

    public static void append(String text) {
        console.append(text);
    }

    public static void append(int number) {
        console.append("" + number);
    }

    public static void append(double number) {
        console.append("" + number);
    }

    public static void append(boolean bool) {
        console.append("" + bool);
    }

    public static String getText() {
        return console.getText();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            log.error(e.getMessage(), e);
        }

        GUIConsole.run("MyGUIConsole", 200, 100, "");

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                java.util.ArrayList<Object> elements = new java.util.ArrayList<Object>();

                Collections.addAll(elements, "Testing", 12, 32.53f, 'c', 0x43, true);

                Iterator<Object> i = elements.iterator();
                Object obj;
                for (obj = i.next(); i.hasNext(); obj = i.next()) {
                    log.info(obj + " --- " + new Date());
                }

            }
        });

    }

}