package com.androcode.barteni.RTAICalendarReforgeAPI;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.tomcat.util.json.JSONParser;

public class ICalendarConverter {

    public static String convertJson(String icalendarString) {
        boolean estAlternance = false;
        boolean firstVEvent = true;
        StringBuilder rs = new StringBuilder();
        String[] lines = icalendarString.split("\\r?\\n");
        String[] parts;

        for(String line : lines){


            if(line.equals("BEGIN:VCALENDAR")){
                rs.append("{");
            }
            if(line.equals("BEGIN:VEVENT")){
                if(firstVEvent) {
                    firstVEvent = false;
                    rs.append("\"VEVENTS\":[{");
                }else{
                    rs.append(",{");
                }
            }

            // For other lines, convert the iCalendar property to JSON format
            if(line.contains("ALTERNANCE")){
                estAlternance = true;
            }
            if(line.contains("DESCRIPTION")){
                if(estAlternance){
                    line = "DESCRIPTION:";
                    estAlternance = false;
                }else{
                    parts = line.split("\\\\n");
                    line = parts[0] + parts[4];
                }
            }

            if(line.contains("\\")){
                line = line.replace("\\", "");
            }


            System.out.println(line);
            parts = line.split(":");




            if(line.equals("END:VCALENDAR")){
                rs.append("],");
            }
            if(line.equals("END:VEVENT")){
                rs.append("\"END\":\"VEVENT\"}");
            }else{
                if (parts.length == 1){
                    rs.append("\"").append(parts[0]).append("\":\"").append("null").append("\",");
                }else{
                    rs.append("\"").append(parts[0]).append("\":\"").append(parts[1]).append("\"");
                    if(!line.equals("END:VCALENDAR")){
                        rs.append(",");
                    }
                }
            }
        }

        rs.append("}");

//        JsonObject jsonObject = JsonParser.parseString(rs.toString()).getAsJsonObject();

        return rs.toString();
    }
}
