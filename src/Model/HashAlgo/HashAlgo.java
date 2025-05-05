package Model.HashAlgo;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;

public interface HashAlgo {

    public String hash(String data);
      
    public String hashFile(String src) ;

}
