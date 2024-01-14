package com.androcode.barteni.RTAICalendarReforgeAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class IcsManager {

    private static String downloadData() {

        //URL Agenda : https://cours23-24.ut-capitole.fr/calendar/export_execute.php?userid=140260&authtoken=4f4b49b29c452d9ab6cff30a6ddf80f363df356b&preset_what=all&preset_time=custom
        String fileURL = "https://ade-production.ut-capitole.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?data=8241fc38732002145f8811789c9c6731bd72d825015315fe66c60d53cab758dbf377b612dec2c5fba5147d40716acb136c03e67b339315cf";
        String defaultPath = "C:\\Users\\Bastien\\Downloads\\";
        String fileName = "ICal.json";

        try {
            URL url = new URL(fileURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream();
                     InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                     BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

                    // Lire le contenu du fichier .ICS
                    StringBuilder icsContent = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        icsContent.append(line).append("\n");
                        //System.out.println(icsContent);
                    }

                    return icsContent.toString();
                }
            } else {
                System.err.println("La requête HTTP a échoué avec le code de réponse : " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getICalData() {

        String icalendarString = downloadData();
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


//            System.out.println(line);
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

//        return rs.toString();
        return new JSONObject(rs.toString());
    }

    public static JSONArray getVEventsByDays() {
        JSONObject ical = getICalData();
        JSONArray vevents = ical.getJSONArray("VEVENTS");

        Map<String, List<JSONObject>> MAPeventsByDay = new HashMap<>();

        // Grouper les événements par jour
        for (Object e : vevents) {
            JSONObject event = (JSONObject) e;
            Date date = convertStringToDate(event.getString("DTSTART"));
            int year = date.getYear() + 1900;
            int month = date.getMonth();
            int day = date.getDate();
            String dateKey = String.format("%04d-%02d-%02d", year, month + 1, day);

            if (!MAPeventsByDay.containsKey(dateKey)) {
                MAPeventsByDay.put(dateKey, new ArrayList<>());
            }

            MAPeventsByDay.get(dateKey).add(event);
        }

        // Convertir le résultat en JSONArray si nécessaire
        JSONArray eventsByDays = new JSONArray();
        for (Map.Entry<String, List<JSONObject>> entry : MAPeventsByDay.entrySet()) {
            JSONObject dayEvents = new JSONObject();
            dayEvents.put("date", entry.getKey());
            dayEvents.put("events", new JSONArray(entry.getValue()));
            eventsByDays.put(dayEvents);
        }

        List<JSONObject> myJsonArrayAsList = new ArrayList<JSONObject>();
        for (int i = 0; i < eventsByDays.length(); i++)
            myJsonArrayAsList.add(eventsByDays.getJSONObject(i));


        //Trie les events par date du plus récent au plus ancien
        Collections.sort(myJsonArrayAsList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject jsonObjectA, JSONObject jsonObjectB) {
                int compare = 0;
                try
                {
                    String keyA = jsonObjectA.getString("date");
                    String keyB = jsonObjectB.getString("date");
                    compare = CharSequence.compare(keyA, keyB);
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
                return compare;
            }
        });

        eventsByDays = new JSONArray();
        for (int i = 0; i < myJsonArrayAsList.size(); i++) {
            eventsByDays.put(myJsonArrayAsList.get(i));
        }


//        System.out.println(rs.toString(2));

        return eventsByDays;
    }

    public static JSONArray getVEventsByMonths() {
        JSONArray eventsByDays = getVEventsByDays();
        JSONArray eventsByMonth = new JSONArray();

        for (int i = 0; i < 12; i++) {
            JSONArray monthEvents = new JSONArray();

            for (int j = 0; j < eventsByDays.length(); j++) {
                JSONObject dayEvents = eventsByDays.getJSONObject(j);
                String month = dayEvents.getString("date").substring(5, 7);
                if (Integer.parseInt(month) == i+1) {
                    monthEvents.put(dayEvents);
                }
            }

            eventsByMonth.put(new JSONObject().put("days", monthEvents));
        }

//        System.out.println(eventsByMonth.toString(2));

        return eventsByMonth;
    }

    private static Date convertStringToDate(String dateString) {
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(4, 6)) - 1; // Les mois dans Java sont de 0 à 11
        int day = Integer.parseInt(dateString.substring(6, 8));
        int hours = Integer.parseInt(dateString.substring(9, 11));
        int minutes = Integer.parseInt(dateString.substring(11, 13));
        int seconds = Integer.parseInt(dateString.substring(13, 15));

        // Créer un objet Date en utilisant les composants extraits
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = String.format("%04d-%02d-%02dT%02d:%02d:%02d", year, month + 1, day, hours, minutes, seconds);

        try {
            return dateFormat.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace(); // Gérer l'erreur de conversion de date
            return null;
        }
    }
}
