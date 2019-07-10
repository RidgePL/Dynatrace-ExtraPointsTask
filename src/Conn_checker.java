import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import redis.clients.jedis.Jedis;


import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.TimerTask;

public class Conn_checker extends TimerTask {

    //Variables
    private int counter;
    private String filename;
    private String url;

    //Elements
    private HSSFWorkbook workbook;
    private HSSFSheet sheet;
    private Jedis database;

    //Constructor
    public Conn_checker(Jedis database, HSSFWorkbook workbook, String url, String path) {
        this.database = database;
        this.counter = 1;
        this.workbook = workbook;
        this.url = url;
        this.filename = path;
        try {
            this.sheet = this.workbook.createSheet("ConnectionTests");
        } catch (IllegalArgumentException e) {
            this.sheet = this.workbook.getSheet("ConnectionTests");
        }

    }

    //Function that checks the connection to specified URL
    private static void check_connection(String url) throws MalformedURLException, IOException {
        URL checked_url = new URL(url);
        URLConnection connection = checked_url.openConnection();
        BufferedReader response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        response.close();
    }

    //Function that is being performed in given intervals (1 min, the file in database life time 5 mins)
    public void run () {

        long start_time = System.nanoTime();

        try {
            check_connection(this.url);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Wrong URL given");
            this.cancel();
        }

        long end_time = System.nanoTime();

        long exec_time = end_time - start_time;
        String exe_time = String.valueOf(exec_time);
        String key = "Sample no. " + String.valueOf(this.counter);

        this.database.set(key, exe_time);
        this.database.expire(key, 300);


        Row row_header = this.sheet.createRow(0);
        Cell header1 = row_header.createCell(0);
        Cell header2 = row_header.createCell(1);
        header1.setCellValue("Sample no.");
        header2.setCellValue("nanoseconds");


        Row row = this.sheet.createRow(counter);
        Cell cell = row.createCell(0);
        Cell cell1 = row.createCell(1);
        cell.setCellValue(counter);
        cell1.setCellValue(exe_time);

        try (OutputStream fileout = new FileOutputStream(filename+ ".xls")) {
            workbook.write(fileout);
        } catch (IOException e) {

        }
        counter++;
    }

    //Getters and setters
    public int get_counter() {
        return counter;
    }

    public void set_counter(int counter) {
        this.counter = counter;
    }

    public String get_filename() {
        return filename;
    }

    public void set_filename(String filename) {
        this.filename = filename;
    }

    public String get_url() {
        return url;
    }

    public void set_url(String url) {
        this.url = url;
    }

    public HSSFWorkbook get_workbook() {
        return workbook;
    }

    public void set_workbook(HSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public HSSFSheet get_sheet() {
        return sheet;
    }

    public void set_sheet(HSSFSheet sheet) {
        this.sheet = sheet;
    }

    public Jedis get_database() {
        return database;
    }

    public void set_database(Jedis database) {
        this.database = database;
    }

}
