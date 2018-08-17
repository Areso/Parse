import com.sun.deploy.security.ValidationState;
import com.sun.mail.imap.protocol.ID;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sun.util.resources.LocaleData;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Parse {

    JFrame frame;
    JTabbedPane tabs;
    MyLabel screenLabel;
    JLabel screenLabel_2;
    JPanel panel;
    JScrollPane screenScroll;
    JScrollPane screenScroll_2;
    JTextField text;
    BufferedImage screen;
    ImageIcon image;
    ImageIcon image2;
    JButton butscreen;
    JButton start;
    JButton stop;
    JButton buy;
    JButton sell;
    JButton hold;
    JButton option;
    JButton info;
  Dialog  dialog;

    int status=0;
    int size=0;
//Парсинг цены с сайта
    public void getPrice() {
        new Thread(new Runnable(){

            @Override
            public void run() {
                while(true) {
                    Document doc = null;
                    try {
                        doc = Jsoup.connect("https://smart-lab.ru/").get();
                    } catch (IOException e) {
                        e.printStackTrace();
                        test.sendSignal("ERROR","Нет соединения с сервером для запроса текущей цены инструмента "+new Date());
                        continue;
                    }

                    StringBuffer buffer = new StringBuffer(doc.text());

                    price=buffer.substring(buffer.indexOf("fSi"), buffer.indexOf("fSi") + 9).split("fSi ")[1];
                    System.out.println(price);
                    if(work==false&&size==0) {
                        text.setText("Текущая цена: " + price);
                    }
                    buffer=null;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }


        }).start();
    }

    volatile String price;

    Rectangle selection;
    Point anchor;
    boolean work;
    int b1,b2,b3;
    Test test;

    JButton trade1;
    JButton trade2;
    JButton trade3;
    boolean append;
    boolean append2;
    static int ID=0;




    Trade tr;
    Thread mythread;


    public static void main(String[] args) {
        Parse parse = new Parse();
        parse.createGUI();
        //Отчистка файлов перед запуском*****************
        try {
            BufferedWriter writer_1 = new BufferedWriter(new FileWriter("tro.tro"));
            writer_1.write("");
            writer_1.flush();
            writer_1.close();
            BufferedWriter writer_2=new BufferedWriter(new FileWriter("trade.tri",false));
            writer_2.write("");
            writer_2.flush();
            writer_2.close();
            BufferedWriter writer_3=new BufferedWriter(new FileWriter("trr.trr"));
            writer_3.write("");
            writer_3.flush();
            writer_3.close();


        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //*****************************************
    }

    void createGUI() {
//        Наполнение главного фрейма
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        dialog=new Dialog(frame,"Settings");
        /**Инициализация почтового модуля
         *
         */
         test=new Test();
       test.save();
        /*--------------------------------------------

         */

        frame = new JFrame("Parse_Signal v_2.0");
        frame.setBounds(1000,5,350,400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        text = new JTextField();
        text.setEditable(false);
        frame.getContentPane().add(text, BorderLayout.SOUTH);
        tabs = new JTabbedPane(JTabbedPane.BOTTOM);
        frame.getContentPane().add(tabs, BorderLayout.CENTER);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.getContentPane().add(panel, BorderLayout.EAST);
//        Наполнение вкладки TAB  и подключение слушателей
        screenLabel = new MyLabel();
        screenLabel_2 = new JLabel();
        screenScroll = new JScrollPane(screenLabel);
        screenScroll_2 = new JScrollPane(screenLabel_2);
        tabs.add("FullScreen", screenScroll);
        tabs.add("Screen_Area", screenScroll_2);
        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JScrollPane pane = (JScrollPane) ((JTabbedPane) e.getSource()).getSelectedComponent();
            }
        });
        tabs.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Определяем индекс выделенной мышкой вкладки
                int idx = ((JTabbedPane) e.getSource()).indexAtLocation(e.getX(), e.getY());
                text.setText("Выбрана вкладка " + idx);
            }
        });
//        Кнопка скрина и слушатель для нее
        butscreen = new JButton("SCREEN");
        start = new JButton("START  ");
        start.addMouseListener(new ListnerButtonStart());
        stop = new JButton("STOP    ");
        stop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                work = false;
                tr.Order_H();
                try {
                    BufferedWriter writer_1 = new BufferedWriter(new FileWriter("tro.tro"));
                    writer_1.write("");
                    writer_1.flush();
                    writer_1.close();
                    BufferedWriter writer_2=new BufferedWriter(new FileWriter("trade.tri",append=false));
                    writer_2.write("");
                    writer_2.flush();
                    writer_2.close();
                    BufferedWriter writer_3=new BufferedWriter(new FileWriter("trr.trr"));
                    writer_3.write("");
                    writer_3.flush();
                    writer_3.close();


                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                size=0;
                status=0;
                ID=0;
                text.setText("Работа остановлена, файлы tri,tro,trr очищены!");
            }
        });
        butscreen.addMouseListener(new ScreenLister());
        panel.add(butscreen);
        panel.add(start);
        panel.add(stop);
        buy = new JButton("BUY       ");
        sell = new JButton("SELL     ");
        hold = new JButton("HOLD    ");
        option=new JButton("SET        ");
        trade1=new JButton("B");
        trade2=new JButton("S");
        trade3=new JButton("H");
        tr=new Trade();
//        Cоздание диалогового окна
        option.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
             dialog.create();
            }
        });
        info=new JButton("INFO      ");
        info.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(frame,"C вопросам и предложениями " +
                        "обращаться \n по e-mail : parsesignal@yandex.ru");
            }
        });
        panel.add(buy);
        buy.addMouseListener(new DefinedColor());
        sell.addMouseListener(new DefinedColor());
        hold.addMouseListener(new DefinedColor());
        //*********Установка цвета кнопок*****************************
        buy.setBackground(new Color(Integer.parseInt(dialog.bcol_1)));
        sell.setBackground(new Color(Integer.parseInt(dialog.scol_1)));
        hold.setBackground(new Color(Integer.parseInt(dialog.hcol_1)));
        //*************************************************************
        panel.add(sell);
        panel.add(hold);
        panel.add(option);
        panel.add(info);
        panel.add(trade1);
        trade1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               // work=false;
               /* if(mythread!=null) {
                    try {
                        mythread.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                */
                tr.Order_B();
                //status=1;
                size=size+Integer.parseInt(dialog.getQuantitytext());
                text.setText("Покупка");
                text.setText("Лотов в сделке: " +size+" Кол-во сделок "+ID);
            }
        });
        panel.add(trade2);

        trade2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /*work=false;
                text.setText("Продажа");
                if(mythread!=null) {
                    try {
                        mythread.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                */
                tr.Order_S();
                //status=-1;
                size=size-Integer.parseInt(dialog.getQuantitytext());
                text.setText("Продажа");
                text.setText("Лотов в сделке: " +size+" Кол-во сделок "+ID);

            }
        });
        panel.add(trade3);
        trade3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tr.Order_H();
                text.setText("Лотов в сделке: " +size+" Кол-во сделок "+ID);
            }
        });
        getPrice();
        frame.setVisible(true);

    }


    class ScreenLister extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            frame.setVisible(false);
            text.setText("Cделан скрин");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            Robot robot = null;
            try {
                robot = new Robot();
            } catch (AWTException e1) {
                e1.printStackTrace();
            }
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            screen = robot.createScreenCapture(new Rectangle(screenSize));
            image = new ImageIcon(screen);
            screenLabel.setIcon(image);
            frame.setVisible(true);
        }
    }

    class MyLabel extends JLabel implements MouseMotionListener, MouseListener {

        public MyLabel() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (selection != null) {
                Graphics2D g2d = (Graphics2D) g;
                BasicStroke pen = new BasicStroke(2); //толщина линии 20
                g2d.setStroke(pen);
                g2d.setColor(Color.red);
                g2d.draw(selection);
            }
        }

        public void mousePressed(MouseEvent e) {
            anchor = e.getPoint();
            selection = new Rectangle(anchor);
        }

        public void mouseDragged(MouseEvent e) {
            selection.setBounds((int) Math.min(anchor.x, e.getX()), (int) Math.min(anchor.y, e.getY()),
                    (int) Math.abs(e.getX() - anchor.x), (int) Math.abs(e.getY() - anchor.y));
            repaint();
        }

        public void mouseReleased(MouseEvent e) {
            repaint();
            text.setText("Область для скана: " + "Ось X " + selection.x + " " + "Ось Y " + selection.y + " " +
                    "Высота" + selection.getHeight() + " " + "Ширина" + selection.getWidth());
        }

        // unused
        public void mouseMoved(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    class ListnerButtonStart extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            frame.setBounds(1000,5,350,400);
            Runnable run = new MyRunnable();
             mythread = new Thread(run);
            mythread.start();
        }
    }

    public class MyRunnable implements Runnable {
        int count = 1;
        int position=0;

        @Override
        public void run() {
            work = true;
            while (work != false) {
                Robot robot = null;
                try {
                    robot = new Robot();
                } catch (AWTException e1) {
                    e1.printStackTrace();
                }
                BufferedImage buf=null;
                try{buf = robot.createScreenCapture(new Rectangle(selection.x, selection.y, selection.width, selection.height));

                    image2 = new ImageIcon(buf);
                } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(frame,"Выделите область и нажмите кнопку START");
                }
                screenLabel_2.setIcon(image2);
                text.setText("Скрин: " + count +" Кол-во сделок: "+ID+" Лотов в сделке: "+size);
                int[] pixels = copyFromBufferedImage(buf);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    text.setText("Прерван поток!");
                }
                count++;
                searchpixel(pixels);
                /*Скрин в файл
                save_screen();
                */
            }
        }

        private void searchpixel(int[] pixels) {
            for(int i=0;i<pixels.length;i++ ){
                if(pixels[i]==(buy.getBackground().getRGB()& 0xFFFFFF)){
                    position = 1;
                }
                else  if(pixels[i]==(sell.getBackground().getRGB()& 0xFFFFFF)) {
                    position = -1;
                }
                else  if(pixels[i]==(hold.getBackground().getRGB()& 0xFFFFFF)) {
                    position = 0;
                }
            }
            if  (position==1&&status!=1&&b1==1) {
                text.setBackground(buy.getBackground());
                if(dialog.isSend_phone()==true) {
                    new SMS().sendSms(dialog.getPhone(), "СИГНАЛ НА ПОКУПКУ " + new Date(), "TEST-SMS", dialog.getLogin(), dialog.getPassword());
               }
                if(dialog.isSend_trade()==true){
                    tr.Order_Buy();
                }
               if(dialog.isSend_mail()==true){
                    test.sendSignal("BUY","Buy in signal at price "+price+new Date());
               }
                status = 1;


            }
            else if  (position==-1&&status!=-1&&b2==2) {
                text.setBackground(sell.getBackground());
               if(dialog.isSend_phone()==true) {
                    new SMS().sendSms(dialog.getPhone(), "СИГНАЛ НА ПРОДАЖУ " + new Date(), "TEST-SMS", dialog.getLogin(), dialog.getPassword());
                }
                if(dialog.isSend_trade()==true){
                    tr.Order_Sell();
                }
                if(dialog.isSend_mail()==true){
                    test.sendSignal("SELL","Sell in signal at price "+price+new Date());
                }
                status=-1;



            }
            else if  (position==0&&status!=0&&b3==3) {
                text.setBackground(hold.getBackground());
               if(dialog.isSend_phone()==true) {
                   new SMS().sendSms(dialog.getPhone(), "БЕЗ ПОЗИЦИИ (ВНЕ РЫНКА) " + new Date(), "TEST-SMS", dialog.getLogin(), dialog.getPassword());
                }
                if(dialog.isSend_trade()==true){
                    tr.Order_HOLD();
                }
                if(dialog.isSend_mail()==true){
                    test.sendSignal("HOLD","HOLD in signal at price "+price+new Date());
                }
                status=0;

            }
        }
/*
private void save_screen(){
    Robot robot = null;
    try {
        robot = new Robot();
    } catch (AWTException e1) {
        e1.printStackTrace();
    }

    BufferedImage bi =robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
         File img=new File("screen_"+count+".png");
    try {
        ImageIO.write(bi,"png",img);

    } catch (IOException e) {
        e.printStackTrace();
    }
}
*/

        private int[] copyFromBufferedImage(BufferedImage bi) {
            int[] pict = new int[bi.getHeight() * bi.getWidth()];
            for (int i = 0; i < bi.getWidth(); i++)
                for (int j = 0; j < bi.getHeight(); j++)
                    pict[i * bi.getHeight() + j] = bi.getRGB(i, j) & 0xFFFFFF; // 0xFFFFFF: записываем только 3 младших байта RGB
            return pict;
        }
    }

    class DefinedColor extends MouseAdapter {

        JButton button;

        @Override
        public void mouseClicked(MouseEvent e) {
            button = (JButton) e.getSource();
            text.setText("Определяем цвет");
            if(button.getText().trim().equals("BUY")){
                b1=0; }
            else if(button.getText().trim().equals("SELL"))
                b2=0;
            else if(button.getText().trim().equals("HOLD"))
                b3=0;
            screenLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            screenLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Robot robot = null;
                    try {
                        robot = new Robot();
                    } catch (AWTException e1) {
                        e1.printStackTrace();
                    }
                    Color color = robot.getPixelColor(e.getPoint().x, e.getPoint().y);
                    text.setText(color.toString());
                    if(button.getText().trim().equals("BUY")&&b1!=1) {
                        buy.setBackground(color);
                        b1=1;
                    }
                    else if(button.getText().trim().equals("SELL")&&b2!=2){
                        sell.setBackground(color);
                        b2=2;
                    }
                    else if(button.getText().trim().equals("HOLD")&&b3!=3) {
                        hold.setBackground(color);
                        b3=3;
                    }
                }
            });
        }
    }
//******************************************************************************************************************************
    /** КОД ДЛЯ ОТПРАВКИ СМС СООБЩЕНИЙ
     *
     */
    public  class SMS {

        public SMS() {
        }

        private void sendSms(String phone, String text, String sender, String name, String password) {
            try {
                System.out.println(phone + " " + text + " " + sender + " " + name + " " + password);
                String authString = name + ":" + password;
                String authStringEnc = Base64.getEncoder().encodeToString(authString.getBytes());

                URL url = new URL("http", "api.smsfeedback.ru", 80, "/messages/v2/send/?phone=%2B" + phone + "&text=" + URLEncoder.encode(text, "UTF-8") + "&sender=" + sender);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setRequestProperty("Authorization", authStringEnc);
                InputStream is = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int numCharsRead;
                char[] charArray = new char[1024];
                StringBuffer sb = new StringBuffer();
                while ((numCharsRead = isr.read(charArray)) > 0) {
                    sb.append(charArray, 0, numCharsRead);
                }
                String result = sb.toString();

                System.out.println("*** BEGIN ***");
                System.out.println(result);
                System.out.println("*** END ***");

            } catch (MalformedURLException ex) {
                System.out.println(ex.toString());
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
    }

//**************************************************************************************************************************
    /**КОД ДИАЛОГОВОГО ОКНА
     *
     */
    public  class Dialog extends JDialog {
        private    JTextField phone;
        private  JTextField mail;
        private    JTextField login;
        private  JTextField password;
        private  JTextField passmail;
        private JLabel text1;
        private JLabel text2;
        private JLabel text3;
        private JLabel text4;
        private JLabel text5;
        private Checkbox boxmail;
        private Checkbox boxphone;
        private Checkbox boxtrade;
        private JButton button;
        private JTextField accounttext;
        private JTextField clientcodetext;
        private JTextField seccodetext;
        private JTextField quantitytext;
        private JTextField delta;
        //private JTextField uppertext;
        //private JTextField lowertext;
        private JLabel accountlab;
        private JLabel clientcodelab;
        private JLabel seccodelab;
        private JLabel quantitylab;
        private JLabel deltalab;
        //private JLabel upperlab;
        //private JLabel lowerlab;
        private String bcol_1,scol_1,hcol_1;



        public Dialog(Frame owner, String title) {
            super(owner, title);
            text1=new JLabel("Phone number ");
            phone=new JTextField(15);
            text2=new JLabel("Mail (Yandex) ");
            text5=new JLabel("Password e-mail");
            text3=new JLabel("Mobile Login ");
            text4=new JLabel("Phone Passwod ");
            login=new JTextField(15);
            password=new JTextField(15);
            passmail=new JTextField(15);
            mail=new JTextField(15);
            boxmail=new Checkbox(  "E-mail signals");
            boxphone=new Checkbox( "Phone signals ");
            boxtrade=new Checkbox( "TS signals (Quik)");
            button=new JButton("SAVE");
             accounttext=new JTextField(10);
            clientcodetext=new JTextField(10);
             seccodetext=new JTextField(10);
            quantitytext=new JTextField(10);
            delta=new JTextField(10);
            //uppertext=new JTextField(10);
             //lowertext=new JTextField(10);
              accountlab=new JLabel(  "Account");
             clientcodelab=new JLabel("Code cl");
              seccodelab=new JLabel(  "Secode");
             quantitylab=new JLabel("Quantity");
             deltalab=new JLabel("Delta    ");
              //upperlab=new JLabel(   "Upper L");
              //lowerlab=new JLabel(   "Lower L");

            loadoption();
        }

        void loadoption() {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader("auth.txt."));
                while(reader.ready()){
                    phone.setText(reader.readLine());
                    login.setText(reader.readLine());
                    password.setText(reader.readLine());
                    mail.setText(reader.readLine());
                    passmail.setText(reader.readLine());
                    accounttext.setText(reader.readLine());
                    clientcodetext.setText(reader.readLine());
                    seccodetext.setText(reader.readLine());
                    quantitytext.setText(reader.readLine());
                    delta.setText(reader.readLine());
                    boxmail.setState(Boolean.parseBoolean(reader.readLine()));
                    boxphone.setState(Boolean.parseBoolean(reader.readLine()));
                    boxtrade.setState(Boolean.parseBoolean(reader.readLine()));
                    bcol_1=reader.readLine();
                    scol_1=reader.readLine();
                    hcol_1=reader.readLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void create() {
            setBounds(1000,5,220,650);
            setLayout(new FlowLayout());
            getContentPane().add(text1);
            getContentPane().add(phone);
            getContentPane().add(text3);
            getContentPane().add(login);
            getContentPane().add(text4);
            getContentPane().add(password);
            getContentPane().add(text2);
            getContentPane().add(mail);
            getContentPane().add(text5);
            getContentPane().add(passmail);
            getContentPane().add(accountlab);
            getContentPane().add(accounttext);
            getContentPane().add(clientcodelab);
            getContentPane().add(clientcodetext);
            getContentPane().add(seccodelab);
            getContentPane().add(seccodetext);
            getContentPane().add(quantitylab);
            getContentPane().add(quantitytext);
            getContentPane().add(deltalab);
            getContentPane().add(delta);
            //getContentPane().add(upperlab);
            //getContentPane().add(uppertext);
            //getContentPane().add(lowerlab);
            //getContentPane().add(lowertext);
            getContentPane().add(boxmail);
            getContentPane().add(boxphone);
            getContentPane().add(boxtrade);
            getContentPane().add(button);
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        BufferedWriter writer=new BufferedWriter(new FileWriter("auth.txt"));
                        writer.write(phone.getText()+"\n"+login.getText()+"\n"+password.getText()+
                                "\n"+mail.getText()+"\n"+passmail.getText()+"\n"+accounttext.getText()+"\n"+clientcodetext.getText()+"\n"+
                                seccodetext.getText()+"\n"+quantitytext.getText()+"\n"+delta.getText()+"\n"+isSend_mail()+"\n"+isSend_phone()+"\n"+
                             isSend_trade()+"\n"+buy.getBackground().getRGB()+"\n"+sell.getBackground().getRGB()+"\n"+hold.getBackground().getRGB());
                        writer.flush();
                        writer.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    dispose();
                }
            });
            setVisible(true);
        }




        public boolean isSend_mail() {
            return boxmail.getState();
        }


        public boolean isSend_phone() {
            return boxphone.getState();
        }

        public boolean isSend_trade() {
            return boxtrade.getState();
        }


        public String  getPhone() {
            return phone.getText();
        }

        public String getMail() {
            return mail.getText();
        }

        public String getLogin() {
            return login.getText();
        }

        public String getPassword() {
            return password.getText();
        }
        public String getMailpass (){
            return passmail.getText();
        }
        public String getAccounttext() {
            return accounttext.getText();
        }

        public String getClientcodetext() {
            return clientcodetext.getText();
        }

        public String getSeccodetext() {
            return seccodetext.getText();
        }

        public String getQuantitytext() {
            return quantitytext.getText();
        }
        public String getDelta() {
            return delta.getText();
        }

   /*     public String getUppertext() {
            return uppertext.getText();
        }

        public String getLowertext() {
            return lowertext.getText();
        }*/
    }

 //-------------------------------------------------------------------------------------------------------------------------------
    /**КОД ДЛЯ ОТПРАВКИ СИГНАЛОВ НА ПОЧТУ
     *
     */

    public  class Test {
        private Message message = null;
        private  String SMTP_SERVER = null;
        private String SMTP_Port = null;
        private String SMTP_AUTH_USER = null;
        private String SMTP_AUTH_PWD = null;
        private String EMAIL_FROM = null;
        private String FILE_PATH = null;
        private String REPLY_TO = null;

        Properties pr = new Properties();

        void save() {
            pr.put("server","smtp.yandex.ru");
            pr.put("port","465");
            pr.put("from",dialog.getMail());
            pr.put("user",dialog.getMail().split("@")[0]);
            pr.put("pass",dialog.getMailpass());
            pr.put("to",dialog.getMail());
            pr.put("replyto","java-online@mail.ru");


        }
        void SendEmail(final String emailTo, final String thema) {
            // Настройка SMTP SSL
            Properties properties = new Properties();
            properties.put("mail.smtp.host", SMTP_SERVER);
            properties.put("mail.smtp.port", SMTP_Port);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            try {
                Authenticator auth = new EmailAuthenticator(SMTP_AUTH_USER,
                        SMTP_AUTH_PWD);
                Session session = Session.getDefaultInstance(properties, auth);
                session.setDebug(false);

                InternetAddress email_from = new InternetAddress(EMAIL_FROM);
                InternetAddress email_to = new InternetAddress(emailTo);
                InternetAddress reply_to = (REPLY_TO != null) ?
                        new InternetAddress(REPLY_TO) : null;
                message = new MimeMessage(session);
                message.setFrom(email_from);
                message.setRecipient(Message.RecipientType.TO, email_to);
                message.setSubject(thema);
                if (reply_to != null)
                    message.setReplyTo(new Address[]{reply_to});
            } catch (AddressException e) {
                System.err.println(e.getMessage());
            } catch (MessagingException e) {
                System.err.println(e.getMessage());
            }
        }
        class EmailAuthenticator extends Authenticator
        {
            private String login   ;
            private String password;
            public EmailAuthenticator (final String login, final String password)
            {
                this.login    = login;
                this.password = password;
            }
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(login, password);
            }
        }

        public boolean sendMessage (final String text)
        {
            boolean result = false;
            try {
                // Содержимое сообщения
                Multipart mmp = new MimeMultipart();
                // Текст сообщения
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setContent(text, "text/plain; charset=utf-8");
                mmp.addBodyPart(bodyPart);
                // Определение контента сообщения
                message.setContent(mmp);
                // Отправка сообщения
                Transport.send(message);
                result = true;
            } catch (MessagingException e){
                // Ошибка отправки сообщения
                System.err.println(e.getMessage());
                    JOptionPane.showMessageDialog(frame,e.getMessage());
            }
            return result;
        }
//----------------------------------------------------------------------------------------------------------------------------------
        /**КОД ОТПРАВКИ СООБЩЕНИЯ
         *
         * @throws FileNotFoundException
         */
        void sendSignal(String thema,String text)  {
            SMTP_SERVER    = pr.getProperty ("server" );
            SMTP_Port      = pr.getProperty ("port"   );
            EMAIL_FROM     = pr.getProperty ("from"   );
            SMTP_AUTH_USER = pr.getProperty ("user"   );
            SMTP_AUTH_PWD  = pr.getProperty ("pass"   );
           REPLY_TO       = pr.getProperty ("replyto");


            String emailTo = pr.getProperty ("to"   );

           SendEmail(emailTo, thema);
            sendMessage(text);
            System.out.println ("Сообщение отправлено "+ EMAIL_FROM);
        }
    }

//--------------------------------------------------------------------------------------------------------------------------------
    /**КОД ОТПРАВКИ ТОРГОВЫХ СИГНАЛОВ
     *
     */

    public class Trade {

        private String field_1 = "ACCOUNT";
        private String field_2 = "CLIENT_CODE";
        private String field_3 = "TYPE";
        private String field_4 = "TRANS_ID";
        private String field_5 = "CLASSCODE";
        private String field_6 = "SECCODE";
        private String field_7 = "ACTION";
        private String field_8 = "OPERATION";
        private String field_9 = "PRICE";
        private String field_10 = "QUANTITY";
        private Properties pr = new Properties();
        private ArrayList<String> list = new ArrayList<>();


        public Trade() {
            list.add(field_1);
            list.add(field_2);
            list.add(field_3);
            list.add(field_4);
            list.add(field_5);
            list.add(field_6);
            list.add(field_7);
            list.add(field_8);
            list.add(field_9);
            list.add(field_10);
            pr.put("ACCOUNT", dialog.getAccounttext());
            pr.put("CLIENT_CODE", dialog.getClientcodetext());
            pr.put("TYPE", "M");
            pr.put("TRANS_ID", "0");
            pr.put("CLASSCODE", "SPBFUT");
            pr.put("SECCODE", dialog.getSeccodetext());
            pr.put("ACTION", "NEW_ORDER");
            pr.put("OPERATION", "B");
            pr.put("PRICE", "58000");
            pr.put("QUANTITY", dialog.getQuantitytext());
        }

        void Order_Buy() {
            pr.setProperty("OPERATION", "B");
            pr.setProperty("PRICE", String.valueOf(Integer.parseInt(price) + Integer.parseInt(dialog.getDelta())));
            pr.setProperty("TRANS_ID", String.valueOf(++ID));
            String str = "";
            if (status == -1) {
                pr.setProperty("QUANTITY", String.valueOf(Integer.parseInt(dialog.getQuantitytext()) * 2));
                for (int i = 0; i < list.size(); i++) {
                    str += list.get(i) + "=" + pr.getProperty(list.get(i)) + "; ";
                }
                size+=Integer.parseInt(dialog.getQuantitytext())*2;
            } else if (status == 0) {
                pr.setProperty("QUANTITY", String.valueOf(Integer.parseInt(dialog.getQuantitytext())));
                for (int i = 0; i < list.size(); i++) {
                    str += list.get(i) + "=" + pr.getProperty(list.get(i)) + "; ";
                }
                size+=Integer.parseInt(dialog.getQuantitytext());
            }
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("trade.tri", append = true));
                writer.write(str + "\r\n");
                writer.flush();
                writer.close();
                text.setText("Покупка...");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }


        void Order_Sell() {
            pr.setProperty("OPERATION", "S");
            pr.setProperty("TRANS_ID", String.valueOf(++ID));
            pr.setProperty("PRICE", String.valueOf(Integer.parseInt(price) - Integer.parseInt(dialog.getDelta())));
            String str = "";
            if (status == 1) {
                pr.setProperty("QUANTITY", String.valueOf(Integer.parseInt(dialog.getQuantitytext()) * 2));
                for (int i = 0; i < list.size(); i++) {
                    str += list.get(i) + "=" + pr.getProperty(list.get(i)) + "; ";
                }
                size-=Integer.parseInt(dialog.getQuantitytext())*2;
            } else if (status == 0) {
                pr.setProperty("QUANTITY", String.valueOf(Integer.parseInt(dialog.getQuantitytext())));
                for (int i = 0; i < list.size(); i++) {
                    str += list.get(i) + "=" + pr.getProperty(list.get(i)) + "; ";
                }
                size-=Integer.parseInt(dialog.getQuantitytext());
            }
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("trade.tri", append = true));
                writer.write(str + "\r\n");
                writer.flush();
                writer.close();
                text.setText("Продажа...");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        void Order_HOLD() {
            pr.setProperty("TRANS_ID", String.valueOf(++ID));
            String str = "";
            if (status == 1) {
                pr.setProperty("QUANTITY", String.valueOf(Integer.parseInt(dialog.getQuantitytext())));
                pr.setProperty("OPERATION", "S");
                pr.setProperty("PRICE", String.valueOf(Integer.parseInt(price) - Integer.parseInt(dialog.getDelta())));
                for (int i = 0; i < list.size(); i++) {
                    str += list.get(i) + "=" + pr.getProperty(list.get(i)) + "; ";
                }
                size-=Integer.parseInt(dialog.getQuantitytext());
            } else if (status == -1) {
                pr.setProperty("QUANTITY", String.valueOf(Integer.parseInt(dialog.getQuantitytext())));
                pr.setProperty("OPERATION", "B");
                pr.setProperty("PRICE", String.valueOf(Integer.parseInt(price) + Integer.parseInt(dialog.getDelta())));
                for (int i = 0; i < list.size(); i++) {
                    str += list.get(i) + "=" + pr.getProperty(list.get(i)) + "; ";
                }
                size+=Integer.parseInt(dialog.getQuantitytext())*2;
            }

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("trade.tri", append = true));
                writer.write(str + "\r\n");
                writer.flush();
                writer.close();
                text.setText("Выход из позиции...");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        //**********************Простые ордера на покупку и продажу*********************
        void Order_B() {
            pr.setProperty("OPERATION", "B");
            pr.setProperty("PRICE", String.valueOf(Integer.parseInt(price) + Integer.parseInt(dialog.getDelta())));
            pr.setProperty("TRANS_ID", String.valueOf(++ID));
            String str = "";
            pr.setProperty("QUANTITY", String.valueOf(Integer.parseInt(dialog.getQuantitytext())));
            for (int i = 0; i < list.size(); i++) {
                str += list.get(i) + "=" + pr.getProperty(list.get(i)) + "; ";
            }
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("trade.tri", append = true));
                    writer.write(str + "\r\n");
                    writer.flush();
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }


        void Order_S() {
            pr.setProperty("OPERATION", "S");
            pr.setProperty("PRICE", String.valueOf(Integer.parseInt(price) - Integer.parseInt(dialog.getDelta())));
            pr.setProperty("TRANS_ID", String.valueOf(++ID));
            String str = "";
            pr.setProperty("QUANTITY", String.valueOf(Integer.parseInt(dialog.getQuantitytext())));
            for (int i = 0; i < list.size(); i++) {
                str += list.get(i) + "=" + pr.getProperty(list.get(i)) + "; ";}
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("trade.tri", append = true));
                    writer.write(str + "\r\n");
                    writer.flush();
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        void Order_H() {
            if(size!=0){
                pr.setProperty("TRANS_ID", String.valueOf(++ID));
            }
            String str = "";
            if (size >0) {
                pr.setProperty("QUANTITY", String.valueOf(Math.abs(size)));
                pr.setProperty("OPERATION", "S");
                pr.setProperty("PRICE", String.valueOf(Integer.parseInt(price) - Integer.parseInt(dialog.getDelta())));
                for (int i = 0; i < list.size(); i++) {
                    str += list.get(i) + "=" + pr.getProperty(list.get(i)) + "; ";
                }
                size-=size;
            } else if (size <0) {
                pr.setProperty("QUANTITY", String.valueOf(Math.abs(size)));
                pr.setProperty("OPERATION", "B");
                pr.setProperty("PRICE", String.valueOf(Integer.parseInt(price) + Integer.parseInt(dialog.getDelta())));
                for (int i = 0; i < list.size(); i++) {
                    str += list.get(i) + "=" + pr.getProperty(list.get(i)) + "; ";
                }
                size+=Math.abs(size);
                System.out.println(size);
            }



            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("trade.tri", append = true));
                writer.write(str + "\r\n");
                writer.flush();
                writer.close();
                text.setText("Выход из позиции...");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            text.setBackground(Color.WHITE);
        }


        //****************************************************************************
    }
}


