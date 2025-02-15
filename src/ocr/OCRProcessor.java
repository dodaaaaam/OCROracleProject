package ocr;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;

public class OCRProcessor {
    public String extractTextFromImage(String imagePath) {
        Tesseract tesseract = new Tesseract();
        String tessdataPath = "C:\\Users\\rin42\\eclipse-workspace\\OCROracleProject\\tessdata";
        tesseract.setDatapath(tessdataPath);  // tessdata 폴더의 절대 경로
        tesseract.setLanguage("eng");  // 언어 설정 (한국어는 "kor")

        try {
        	File imageFile = new File(imagePath);  // 이미지 파일 경로 설정
            if (!imageFile.exists()) {
                System.out.println("이미지 파일을 찾을 수 없습니다: " + imagePath);
                return null;
            }

            // OCR 실행
            String textResult = tesseract.doOCR(imageFile);
            //System.out.println("추출 text: " + textResult);
            return textResult;
        } catch (TesseractException e) {
            e.printStackTrace();
            return null;
        }
    }
}
