package com.lixinyu.cooperativecoding.util;

import com.lixinyu.cooperativecoding.model.Output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

public class Compiler {
    public Compiler() {
    }


    public File write(String text, String path) throws Exception{
        File file = new File(path);
        PrintStream ps = new PrintStream(new FileOutputStream(file));
        ps.append(text);
        ps.close();
        return file;
    }

    public Output execute(String name,File file,String path, String type, String[] inputs) throws Exception {
        Output output = new Output();

        //Docker即时沙箱环境编译
        String container_name = name+System.currentTimeMillis();
        //docker run --rm -v {path}:/runtime ubuntu:sandbox /bin/bash -c "chmod +x ./run.sh;./run.sh"
        String[] cmd={
                "docker",
                "run",
                "-i",
                "--rm",
                "--name",
                container_name,
                "--cpus",
                "1",
                "-m",
                "64M",
                "--memory-reservation",
                "8M",
                "--stop-timeout",
                "1",
                "-v",
                path+":/runtime",
                "ubuntu:sandbox",
                "/bin/bash",
                "-c",
                "chmod +x ./run.sh;./run.sh"
        };
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        Process process = processBuilder.start();

        //开辟线程杀死container
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    new ProcessBuilder("docker","stop",container_name).start();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, 8000);
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