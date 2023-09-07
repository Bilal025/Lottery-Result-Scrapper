
package Main;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StoreDB {
    public static void main(String[] args) {    
        String dbIP = "localhost:3306";
        String dbName = "lottery";
        String username = "root";
        String password = "";
        
//        String lottery ;
        String siteUrl = "https://www.lankayp.com/dlb/dlb-results";


        try {
            String url = "jdbc:mysql://" + dbIP + "/" + dbName;
            Connection connection = DriverManager.getConnection(url, username, password);

            System.out.println("Connected to SQL server successfully!");
            
            String tableExistsQuery = "SHOW TABLES LIKE 'LotteryResults'";
            PreparedStatement tableExistsStatement = connection.prepareStatement(tableExistsQuery);
            ResultSet resultSet = tableExistsStatement.executeQuery();
            boolean tableExists = resultSet.next();
            
            while(resultSet.next()){
                System.out.println(resultSet.getInt(1));
            }
            
            if (!tableExists) {
            String createTable = "CREATE TABLE LotteryResults ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "lotteryName VARCHAR(200),"
                    + "drawDate VARCHAR(200),"
                    + "drawNumber VARCHAR(50),"
                    + "lotteryResult VARCHAR(50)"
                    + ")";
            
            PreparedStatement createTableStatement = connection.prepareStatement(createTable);
            createTableStatement.execute();
            createTableStatement.close();
            }
           
            Document docResult = Jsoup.connect(siteUrl).get();

//            Elements lotteryDate = docResult.select("div.lotto_title");
              Elements lotteryResults = docResult.select("div.lotto_con");
//            System.out.println(lotteryResults.select("div.lotto_no_r"));
            

            String insertQuery = "INSERT INTO LotteryResults (lotteryName, drawDate, drawNumber, lotteryResult) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
    
            for (Element result : lotteryResults.select("div.lotto_con")) {
                
                String lotteryDate = result.select("div.lotto_title").text();
                String[] lotteryName = result.select("div.numbers_title").text().split("Result");
                String lotteryResult = result.select("div.lotto_no_r").text();
                System.out.println("Lottery: " + lotteryName[0] + "Draw Date: " + lotteryDate + "Draw Number: " + lotteryName[1]+ ", Result: "+ lotteryResult);
                
                insertStatement.setString(1, lotteryName[0]);
                insertStatement.setString(2, lotteryDate);
                insertStatement.setString(3, lotteryName[1]);
                insertStatement.setString(4, lotteryResult);

                insertStatement.executeUpdate();
                
                }
            
            insertStatement.close();
            connection.close();
            } catch (Exception e) {
            System.out.println("Failed! Error message: " + e);
        }
    }
}
