package com.lixinyu.cooperativecoding.util;


import com.lixinyu.cooperativecoding.model.Output;
import java.io.File;
import java.io.OutputStream;

public class Compiler {
    public static Output execute(File file,String path, String type, String[] inputs) throws Exception {

        String target = path+"/target/program";
        Output output = new Output();
        ProcessBuilder processBuilder = new ProcessBuilder("build", target, file.getPath(), type,path);
        Process process = processBuilder.start();
        OutputStream outputStream = process.getOutputStream();
        //将缓冲区的内容写入到输出流中
        for (String input : inputs) {
            outputStream.write(input.getBytes());
            outputStream.write(" ".getBytes());
        }
        outputStream.close();
        output.setError(streamToString(process.getErrorStream()));
        output.setOutput(streamToString(process.getInputStream()));
        process.waitFor();
        process.destroy();
        return output;
    }

    private static String streamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
 }
