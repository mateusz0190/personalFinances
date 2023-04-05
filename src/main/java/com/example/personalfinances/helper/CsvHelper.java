package com.example.personalfinances.helper;

import com.example.personalfinances.model.Category;
import com.example.personalfinances.model.Expense;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvHelper {
    public static String TYPE = "text/csv";
    static String[] HEADERs = {
            //0                 1                       2                  3    4               5
            "Data transakcji", "Data księgowania", "Dane kontrahenta", "Tytuł", "Nr rachunku", "Nazwa banku",
            // 6                7               8                                   9
            "Szczegóły", "Nr transakcji", "Kwota transakcji (waluta rachunku)", "Waluta",
            //  10                                  11          12                          13      14
            "Kwota blokady/zwolnienie blokady", "Waluta", "Kwota płatności w walucie", "Waluta", "Konto",
            // 15           16                  17
            "Bank", "Saldo po transakcji", "Waluta"
    };

    public static boolean hasCSVFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    private static Iterable<CSVRecord> readCSV(InputStream inputStream) {
        Iterable<CSVRecord> records;
        try (InputStreamReader bufferedReader = new InputStreamReader(inputStream, "windows-1250")) {
            CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.newFormat(';'));
            records = csvParser.getRecords();
        } catch (IOException e) {
            throw new RuntimeException("Failed to import");
        }
        return records;

    }

    public static List<Expense> CSVtoExpense(InputStream inputStream) throws IOException {

        List<Expense> expenses = new ArrayList<>();
        int counter = 0;
        Iterable<CSVRecord> records = readCSV(inputStream);

        for (CSVRecord record :
                    records) {

                int data = 0;
                int description = 2;
                if (counter >= 1) {

                    if (record.size() < 15 || (record.get(0).isEmpty()&& record.get(1).isEmpty())) {

                        return expenses;
                    }
                    counter++;
                    if (record.get(0).length() < 8) {
                        data++;
                    }
                    if (record.get(2).length() < 11) {
                        description++;
                    }
                    Expense expense = Expense.builder()
                            .transactionData(record.get(data))
                            .transactionDescription(record.get(description))
                            .transactionTitle(record.get(3))
                            .transactionValue(BigDecimal
                                    .valueOf(Double
                                            .valueOf(record.get(8).replace(',', '.'))))
                            .bankName(record.get(15))
                            .accountNumber(record.get(14))
                            .transactionId(record.get(7))
                            .build();
                    expenses.add(expense);

                } else if (record.get(0).contains(HEADERs[0])) {
                    counter++;
                }
            }
            return expenses;

    }

    public static Set<Category> CSVtoCategory(InputStream inputStream) throws IOException {
        Set<Category> categories = new HashSet<>();
        Iterable<CSVRecord> records = readCSV(inputStream);
            for (CSVRecord csvRecord :
                    records) {
                Category category = Category.builder().name(csvRecord.get(0)).build();
                categories.add(category);
            }
        return categories;
    }
}
