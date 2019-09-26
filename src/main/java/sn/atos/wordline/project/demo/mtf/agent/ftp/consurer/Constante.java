package sn.atos.wordline.project.demo.mtf.agent.ftp.consurer;

import java.io.File;
import java.util.Optional;

public class Constante {
   public static final String SERVER_ADDRESS = "localhost";
   public static final int SERVER_PORT = 9090;
   public static final int CLIENT_SERVER_PORT = 9091;
   public static final int BUFFER_SIZE = 4096;
//   public static final int BUFFER_SIZE = 1024;
   public static final int BUFFER_SIZE8 = 1008;

   public static final String FILE_DIRECTORY = "D:\\mtf\\";
   public static final String FILE_DIRECTORY_CLIENT = FILE_DIRECTORY + "client\\";
   public static final String FILE_DIRECTORY_CLIENT_RECEIVED = FILE_DIRECTORY + "client\\recus\\";
   public static final String FILE_DIRECTORY_SERVER = FILE_DIRECTORY + "server\\";

  public static byte[] stringTobyte(byte[] bytes, String s){
    for (int i = 0; i < s.getBytes().length; i++) {
      bytes[i] = s.getBytes()[i];
    }
    return bytes;
  }
   public static String byteToString(byte[] bytes){
      String str = new String(bytes);
      String clean = "";
      for (int i = 0; i < str.length() ; i++) {
         if(str.charAt(i) != 0){
            clean += str.charAt(i);
         }else {
            i = str.length();
         }
      }
      return clean;
   }
   public static File checkFile(File file){
     String extension, name = file.getAbsolutePath();
     extension = getExtension(file.getAbsolutePath()).orElse("");
     if(!"".equalsIgnoreCase(extension)){
       extension = "." + getExtension(file.getAbsolutePath()).orElse("");
     }
     name = name.replaceFirst(extension, "");
      if(file.exists()){
         int i = 1;
         while(file.exists()){
            file = new File(name + "(" + i + ")" + extension);
            i++;
         }
      }
      return file;
   }
   public static Optional<String> getExtension(String filename) {
      return Optional.ofNullable(filename)
              .filter(f -> f.contains("."))
              .map(f -> f.substring(filename.lastIndexOf(".") + 1));
   }
}
