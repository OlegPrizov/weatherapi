package org.example;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherApi {

    public static void main(String[] args) throws Exception {
        // Ключ API
        String accessKey = "9843d4e1-d815-48f1-823d-7c92e4c85039";
        String url = "https://api.weather.yandex.ru/v2/forecast?lat=55.75&lon=37.62&limit=2";

        // Создаю запрос
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Yandex-Weather-Key", accessKey)
                .GET()
                .build();

        // Отправляю запрос и получаю ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        // Преобразую в нужный формат
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);

        // Вывожу полный ответ
        System.out.println("Полный ответ от API:");
        String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        System.out.println(prettyResponse);

        // Вывожу фактическую температуру
        System.out.println("Фактическая температура (в блоке fact {temp}):");
        JsonNode factNode = rootNode.path("fact");
        int factTemperature = factNode.path("temp").asInt(); // Извлекаем значение температуры
        System.out.println(factTemperature + "°C");

        // Дальше надо извлечь среднюю температуру из прогнозов (я беру 2 дня)
        // Я не до конца понял, в каком блоке температура за день, но для выполнения задания
        // я буду брать температуру из блока day.temp_avg – тут средняя за световой день
        JsonNode forecasts = rootNode.path("forecasts");

        int totalTemp = 0;
        int countDays = 0;

        for (JsonNode forecast : forecasts) {
            String date = forecast.path("date").asText();
            JsonNode dayNode = forecast.path("parts").path("day");
            int tempAvg = dayNode.path("temp_avg").asInt();
            totalTemp += tempAvg;
            countDays++;

            System.out.println("Средняя температура за день " + countDays + " (" + date + "): " + tempAvg + "°C");
        }

        double averageTemp = totalTemp / (double) countDays;
        System.out.println("Средняя температура за " + countDays + " дня(ей): " + averageTemp + "°C");
    }
}
