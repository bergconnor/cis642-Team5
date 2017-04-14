package edu.ksu.cis.waterquality;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class FileManager {

    private Context _context;

    public FileManager(Context context) {
        _context = context;
    }

    public void writeFile(String filename, String data) {
        FileOutputStream outputStream;
        try {
            outputStream = _context.openFileOutput(filename, _context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public String readFile(String filename) {
        FileInputStream inputStream;
        String data = "";
        try {
            inputStream = _context.openFileInput(filename);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStream.close();
            data = stringBuilder.toString();
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return data;
    }

    public void clearFile(String filename) {
        FileOutputStream outputStream;
        try {
            outputStream = _context.openFileOutput(filename, _context.MODE_PRIVATE);
            outputStream.write("".getBytes());
            outputStream.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
