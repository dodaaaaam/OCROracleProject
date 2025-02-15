import ocr.OCRProcessor;
import db.OracleDBConnector;
import java.util.Scanner;

import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        boolean continueFlag = true;
        int num = 1;
        Scanner scan = new Scanner(System.in);  // 루프 외부에서 Scanner 생성

        while (continueFlag) {
            String imagePath = "C:\\Users\\rin42\\eclipse-workspace\\OCROracleProject\\tessdata\\ticket" + num + ".png";  // OCR 처리할 이미지 경로
            num++;

            // OCR을 이용하여 텍스트 추출
            OCRProcessor ocrProcessor = new OCRProcessor();
            String extractedText = ocrProcessor.extractTextFromImage(imagePath);

            // Oracle DB에 텍스트 저장
            if (extractedText != null && !extractedText.isEmpty()) {
                OracleDBConnector dbConnector = new OracleDBConnector();
                dbConnector.saveOCRData(extractedText);
                System.out.println("continue >> 1");
                System.out.println("finish >> 0");

                // 입력 대기
                if (scan.hasNext()) {  // 입력이 있을 경우만 처리
                    String a = scan.next();
                    if (a.equals("0")) {
                        continueFlag = false;
                        System.out.println("end");
                    }
                }
            } else {
                System.out.println("텍스트가 추출되지 않았습니다.");
            }
            if (num == 8) {
                continueFlag = false;
                break;
            }
        }

        scan.close();  // 루프가 끝난 후 Scanner 닫기
        if (num == 8) {
            System.out.println("모든 티켓이 저장되었습니다.");
        }
    }
}

