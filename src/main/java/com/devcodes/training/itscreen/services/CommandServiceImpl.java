package com.devcodes.training.itscreen.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
public class CommandServiceImpl implements CommandService {

    @Value(value = "${file.upload-dir}")
    private String pathImage;

    @Override
    public String doExec(String args) {
        try {

            String command = String.format("python3 forJava.py -r %s/%s ",pathImage,args);
            String output;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream()))) {
                output =  br.lines().collect(Collectors.joining(System.lineSeparator()));
            }
            return output;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
