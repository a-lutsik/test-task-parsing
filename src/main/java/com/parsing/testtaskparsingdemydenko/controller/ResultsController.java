package com.parsing.testtaskparsingdemydenko.controller;

import com.parsing.testtaskparsingdemydenko.service.ReadData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

/**
* Remarks:
* 1. You have to give a human readable name to the methods. This approach avoids useless comments.
* 2. ReadData is an incorrect name for class. It mustn't be a verb! (For example: DataReader)
* 3. Path must be plural or singular in whole project (/results, /start-times, /finish-times)
*/

@RestController
@RequestMapping("/competition")
public class ResultsController {
    private final ReadData readData;

    public ResultsController(ReadData readData) {
        this.readData = readData;
    }
//method returns 10 leaders participants, using the list of their ID.
    @GetMapping("/results")
    @ResponseBody
    public ArrayList<String> getResultMap(){
        return readData.sortMap();
    }

//extra methods to get all the participants with their start/finish times via key-value view.
    @GetMapping("/start-time")
    @ResponseBody
    public Map<String, String> getStartTimes(){
        return readData.readAndRefactorStartLogs();
    }
    @GetMapping("/finish-time")
    @ResponseBody
    public Map<String, String> getFinishTimes(){
        return readData.readFinishLogsFromFile();
    }
}
