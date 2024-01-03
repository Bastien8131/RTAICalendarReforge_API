package com.androcode.barteni.RTAICalendarReforgeAPI;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IcsDownload {

    public static void main(String[] args) {
        String calendar = getICalJson();
        System.out.println(calendar);

    }

    public static String getICalJson() {

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




                    return ICalendarConverter.convertJson(icsContent.toString());





                }
            } else {
                System.err.println("La requête HTTP a échoué avec le code de réponse : " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "[ERROR]";
    }
}
