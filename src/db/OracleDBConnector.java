package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;

public class OracleDBConnector {
    private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1";  // Oracle DB URL
    private static final String DB_USER = "SYSTEM";  // DB 사용자 이름
    private static final String DB_PASSWORD = "0187";  // DB 비밀번호

    public void saveOCRData(String ocrData) {
    	Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;

        try {
            // 1. Oracle JDBC 드라이버 로드
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // 2. DB 연결
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 3. 테이블 생성 및 존재 여부 확인
            if (!isTableExists(connection, "OCR_DATA_TABLE")) {
            	String createTableSQL = "CREATE TABLE ocr_data_table (" +
                        "name CLOB," +
                        "departure CLOB," +
                        "arrival CLOB," +
                        "class CLOB" +
                        ")";
				statement = connection.createStatement();
				statement.execute(createTableSQL);
				System.out.println("테이블 생성 성공");
            }else {
            	System.out.println("테이블 이미 존재 >> 건너뛰기");
            }
            
            // 4. 데이터 슬라이싱
            String[] lines = ocrData.split("\n");
            
            String[][] wordsPerLine = new String[lines.length][];  // 2차원 배열 선언

            for (int i = 0; i < lines.length; i++) {
                wordsPerLine[i] = lines[i].split(" ");  // 띄어쓰기를 기준으로 자르기
            }
            
            // debug
            /*
             System.out.println("줄과 단어별로 나눈 결과:");
            for (int i = 0; i < wordsPerLine.length; i++) {
                System.out.println("줄 " + (i + 1) + ":");
                for (String word : wordsPerLine[i]) {
                    System.out.println("  단어: " + word);
                }
            } 
             */

            // 5. 데이터 삽입 SQL
            String insertSQL = "INSERT INTO ocr_data_table (name, departure, arrival, class) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertSQL);
            String[] categories = {"NAME", "DEPARTURE", "ARRIVAL", "CLASS"};

            String[] values = new String[4];  // 각 카테고리 값 저장

            for (int i = 0; i < wordsPerLine.length; i++) {
            	if (wordsPerLine[i] == null || wordsPerLine[i].length < 2) {
                    continue;  // 비정상적인 데이터는 무시
                }
                String key = wordsPerLine[i][0];  // 현재 키
                String value = wordsPerLine[i][1];  // 현재 값
                
                // key와 value가 null 또는 빈 문자열인지 확인
                if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                    continue;  // 비어 있으면 무시
                }

                // 카테고리와 비교하여 값 설정
                for (int j = 0; j < categories.length; j++) {
                    if (key.equals(categories[j])) {
                        values[j] = value;
                        break;
                    }
                }
            }

            // PreparedStatement에 값 설정
            preparedStatement.setString(1, values[0]);  // name
            preparedStatement.setString(2, values[1]);  // departure
            preparedStatement.setString(3, values[2]);  // arrival
            preparedStatement.setString(4, values[3]);  // class

            // 데이터 삽입 실행
            preparedStatement.executeUpdate();
            System.out.println("데이터가 성공적으로 삽입되었습니다.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // 테이블 존재 여부를 확인하는 메서드
    private static boolean isTableExists(Connection connection, String tableName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, tableName.toUpperCase(), null);
            return resultSet.next();  // 결과가 존재하면 테이블이 있는 것
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}