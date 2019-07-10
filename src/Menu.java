import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import redis.clients.jedis.Jedis;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class Menu {

    //GUI components
    private JFrame main_window;
    private JLabel samples;
    private JLabel filepath;
    private JButton url_button;
    private JButton name_changer;
    private JButton start;
    private JButton stop;
    private JButton dir_chooser;

    //Elements
    private Jedis jedis;
    private HSSFWorkbook workbook;
    private Timer timer;
    private JFileChooser directory;

    //Variables
    private int samples_amount;
    private String url;
    private String name;
    private String path;



    Menu(Jedis jedis, HSSFWorkbook workbook) {

        //setting default values of elements and variables
        this.jedis = jedis;
        this.workbook = workbook;
        this.samples_amount = 0;
        this.url = "https://www.dynatrace.com/";
        this.name = "ConnectionTestResults";
        this.path = "D:\\";
        this.main_window = new JFrame("Performance checker");
        this.start = new JButton("Start connection test");
        this.stop = new JButton("Stop connection test");
        this.samples = new JLabel();
        this.filepath = new JLabel();
        name_changer = new JButton("Change file name");
        this.directory = new JFileChooser("D:\\");
        this.url_button = new JButton("Change URL");
        this.dir_chooser = new JButton("Choose directory");

        //Settings for Main Window
        this.main_window.setSize(500, 400);
        this.main_window.setContentPane(new JLabel(new ImageIcon("th.jpg")));
        this.main_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.main_window.setResizable(false);


        //Settings for Start Button
        this.start.setEnabled(true);
        this.start.setToolTipText("Starts the connection test to a specified website");
        this.start.setOpaque(false);
        this.start.setContentAreaFilled(false);
        this.start.setBounds(0, 50, 200, 50);
        this.start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start.setEnabled(false);
                stop.setEnabled(true);
                url_button.setEnabled(false);
                name_changer.setEnabled(false);
                dir_chooser.setEnabled(false);
                start_connection_test();
            }
        });

        //Settings for Stop Button
        this.stop.setEnabled(false);
        this.stop.setBounds(200, 50, 200, 50);
        this.stop.setOpaque(false);
        this.stop.setContentAreaFilled(false);
        this.stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stop.setEnabled(false);
                timer.cancel();
                timer.purge();
                start.setEnabled(true);
                url_button.setEnabled(true);
                name_changer.setEnabled(true);
                dir_chooser.setEnabled(true);
                samples_amount = 0;
            }
        });

       //Settings for Label displaying samples amount
        this.samples.setBounds(1, 1, 200, 15);
        this.samples.setOpaque(false);
        this.samples.setText("Samples collected: " + String.valueOf(this.samples_amount));

        //Settings for Label displaying the filepath
        this.filepath.setBounds(1,16,5000,15);
        this.filepath.setText("Saving results to:  " + this.path+ "\\" + this.name);

       //Setting for Button that changes file name
        name_changer.setOpaque(false);
        name_changer.setBounds(0,150,200,50);
        name_changer.setContentAreaFilled(false);
        name_changer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                name_action();
                start.setEnabled(true);
                stop.setEnabled(false);
            }
        });

        //Settings for Button that changes URL
        this.url_button.setEnabled(true);
        this.url_button.setOpaque(false);
        this.url_button.setBounds(0,100,200,50);
        this.url_button.setContentAreaFilled(false);
        this.url_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                url_action();
                start.setEnabled(true);
                stop.setEnabled(false);
            }
        });

        //Settings for Directory chooser and Button that changes it
        this.directory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.dir_chooser.setEnabled(true);
        this.dir_chooser.setOpaque(false);
        this.dir_chooser.setContentAreaFilled(false);
        this.dir_chooser.setBounds(0,200,200,50);
        this.dir_chooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               int returned =  directory.showOpenDialog(main_window);
               if(returned == JFileChooser.APPROVE_OPTION){
                   set_path(directory.getSelectedFile().getAbsolutePath());
                   filepath.setText("Saving results to:  " + path + "\\" + name);
                   main_window.repaint();
               }
            }
        });

        //Adding all components to Main Window
        this.main_window.add(this.filepath);
        this.main_window.add(this.dir_chooser);
        this.main_window.add(this.name_changer);
        this.main_window.add(this.url_button);
        this.main_window.add(this.samples);
        this.main_window.add(this.start);
        this.main_window.add(this.stop);
        this.main_window.pack();
        this.main_window.setVisible(true);
    }

    //Function that starts the connection tests
    private void start_connection_test() {
        String path = get_path();
        if(!path.endsWith("\\")){
            path += "\\";
        }
        path += get_name();
        Conn_checker conn_checker = new Conn_checker(this.jedis, this.workbook,this.url, path);
        this.timer = new Timer();
        timer.schedule(conn_checker, 0, 60000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                set_samples_amount(check_samples_no(conn_checker));
                samples.setText("Samples collected: " + String.valueOf(samples_amount - 1));
                main_window.repaint();
            }
        }, 0, 60000);
    }

    //Function that handles URL changes
    private void url_action(){
        String url_changer = JOptionPane.showInputDialog("Insert URL to check: ");
        set_url(url_changer);
    }

    //Function that handles Filename changes
    private void name_action(){
    String name_changer = JOptionPane.showInputDialog("Insert filename of diagnostic file: ","ConnectionTestResults");
    if(name_changer!= null)
    set_name(name_changer);
    filepath.setText("Saving results to:  " + this.path+ "\\" + this.name);
    main_window.repaint();
}

    //Function that checks current samples amount
    private int check_samples_no(Conn_checker conn_checker) {
        return conn_checker.get_counter();
    }

    //Getters and setters

    public JFrame get_main_window() {
        return main_window;
    }

    public void set_main_window(JFrame main_window) {
        this.main_window = main_window;
    }

    public Jedis get_jedis() {
        return jedis;
    }

    public void set_jedis(Jedis jedis) {
        this.jedis = jedis;
    }

    public HSSFWorkbook get_workbook() {
        return workbook;
    }

    public void set_workbook(HSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public Timer get_timer() {
        return timer;
    }

    public void set_timer(Timer timer) {
        this.timer = timer;
    }

    public int get_samples_amount() {
        return samples_amount;
    }

    public void set_samples_amount(int samples_amount) {
        this.samples_amount = samples_amount;
    }

    public JLabel get_samples() {
        return samples;
    }

    public void set_samples(JLabel samples) {
        this.samples = samples;
    }

    public String get_url() {
        return url;
    }

    public void set_url(String url) {
        this.url = url;
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
    }

    public String get_path() {
        return path;
    }

    public void set_path(String path) {
        this.path = path;
    }

    public JButton get_url_button() {
        return url_button;
    }

    public void set_url_button(JButton url_button) {
        this.url_button = url_button;
    }

    public JButton get_path_changer() {
        return name_changer;
    }

    public void set_path_changer(JButton path_changer) {
        this.name_changer = path_changer;
    }

    public JButton get_start() {
        return start;
    }

    public void set_start(JButton start) {
        this.start = start;
    }

    public JButton get_stop() {
        return stop;
    }

    public void set_stop(JButton stop) {
        this.stop = stop;
    }

    public JFileChooser get_directory() {
        return directory;
    }

    public void set_directory(JFileChooser directory) {
        this.directory = directory;
    }

    public JButton get_dir_chooser() {
        return dir_chooser;
    }

    public void set_dir_chooser(JButton dir_chooser) {
        this.dir_chooser = dir_chooser;
    }


}
