import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import redis.clients.jedis.Jedis;


public class MetricsApplication {

    public static void main(String[] args) {

        HSSFWorkbook workbook = new HSSFWorkbook();
        Jedis jedis = new Jedis();
        Menu Menu = new Menu(jedis, workbook);


    }
}
