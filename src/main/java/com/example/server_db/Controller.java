package com.example.server_db;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

@RestController("/")
public class Controller {

    @Autowired
    private myrepository repository;

    @GetMapping("getValue")
    public String getValue(@RequestParam("from") String fromCurrency , @RequestParam("to") String toCurrency, @RequestParam("date") String date) {
        return repository.getValueByDateAndFromRateAndToRate(date,fromCurrency,toCurrency);

    }


    @PostMapping("save")
    public String saveData(@RequestBody(required = false)Map<String, Object> requestData) {
        // get url
        String url = (String) requestData.get("url");
        if (url != null) {
            // extractCSV
            try {
                InputStream inputStream = new URL(url).openStream();
                CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));
                // saveCSV in DB
                List<ExchangeRate> newExchangeRateList = new ArrayList<>();
                List<String[]> csvRecords = csvReader.readAll();
                for (String[] csvRecord : csvRecords) {
                    String date = csvRecord[0];
                    String fromRate = csvRecord[1];
                    String toRate = csvRecord[2];
                    String value = csvRecord[3];
                    ExchangeRate existingRate = repository.getByDateAndFromRateAndToRate(date, fromRate, toRate);
                    if (existingRate == null) {
                        ExchangeRate exchangeRate = new ExchangeRate();
                        exchangeRate.setDate(date);
                        exchangeRate.setFromRate(fromRate);
                        exchangeRate.setToRate(toRate);
                        exchangeRate.setValue(value);
                        newExchangeRateList.add(exchangeRate);
                    }
                }
                repository.saveAll(newExchangeRateList);


            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (CsvValidationException e) {
                throw new RuntimeException(e);
            } catch (CsvException e) {
                throw new RuntimeException(e);
            }
        }

        return "success";
    }


    @PostMapping("updatevalue")
    public String saveData(@RequestParam("from") String fromCurrency , @RequestParam("to") String toCurrency, @RequestParam("date") String date, @RequestParam("value") String value) {
        ExchangeRate currentExchangeRate = repository.getByDateAndFromRateAndToRate(date,fromCurrency, toCurrency);
        if (currentExchangeRate != null) {
            currentExchangeRate.setValue(value);
            repository.save(currentExchangeRate);
            return value;
        } else {
            return "no found";
        }

    }

}
