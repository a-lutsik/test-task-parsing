package com.parsing.testtaskparsingdemydenko.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ReadData {
    @Value("${load.start.path}")
    private String startLogsPath;
    @Value("${load.finish.path}")
    private String finishLogsPath;
/* method reads start logs from file for each participant, parse necessary info, namely participantID and start_time
   and change timezone to get correct data.
 */
    public Map<String, String> readAndRefactorStartLogs(){
        Map<String, String> timesTap = new LinkedHashMap<>();

        try (Stream<String> linesStart = Files.lines(Path.of(startLogsPath))) {
            timesTap = linesStart
                    .collect(Collectors.toMap(
                            k -> k.substring(4, 16),
                            v -> {
                                DateFormat parsingTimezone = new SimpleDateFormat("yyMMddHHmmss");
                                parsingTimezone.setTimeZone(TimeZone.getTimeZone("UTC"));

                                DateFormat outputTimezone = new SimpleDateFormat("yyMMddHHmmss");
                                outputTimezone.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));

                                try {
                                    return parsingTimezone.format(outputTimezone.parse(v.substring(20, 32)));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            },
                            ((k, v) -> k), //merge function.
                            LinkedHashMap::new
                    ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return timesTap;
    }

//  method reads finish logs from file for each participant, parse participantID and finish_time.
    public Map<String, String> readFinishLogsFromFile(){
        Map<String, String> timeMap = new LinkedHashMap<>();

        try (Stream<String> linesStart = Files.lines(Path.of(finishLogsPath))) {
            timeMap = linesStart
                    .collect(Collectors.toMap(
                            k -> k.substring(4, 16),
                            v -> v.substring(20, 32),
                            ((k, v) -> v) //merge function.
                    ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return timeMap;
    }
    public Map<String, String> getMergedMap(){
        Map<String, String> mergedMap = Stream.of(readAndRefactorStartLogs(),
                readFinishLogsFromFile())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> {
                            DateFormat format = new SimpleDateFormat("yyMMddHHmmss");
                            Date start, finish;
                            try{
                                start = format.parse(v1);
                                finish = format.parse(v2);
                                return String.valueOf(finish.getTime() - start.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return null;
                            }
                        },
                        LinkedHashMap::new
                ));
        return mergedMap;
    }
    public ArrayList<String> sortMap(){
        Map<String, String> sortedMap = getMergedMap().entrySet().stream()
                .sorted((k1, k2) -> -k2.getValue().compareTo(k1.getValue()))
                .filter(x -> x.getValue().length() == 8)
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey,
                Map.Entry::getValue,((k, v) -> v),  LinkedHashMap::new));
        ArrayList<String> listOfLeadersId = new ArrayList<>(sortedMap.keySet());
        return listOfLeadersId;
    }
}
