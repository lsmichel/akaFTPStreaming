package sn.atos.wordline.project.demo.mtf.agent.ftp.server.utiles;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class PropertiesHelper {

    
    public static Properties getProperties(String inputFilePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(inputFilePath)) {
            properties.load(fileInputStream);
        }
        return properties;
    }

    
    public static void saveProperties(Properties properties, String outputFilePath) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {
            properties.store(fileOutputStream);
        }
    }
}
