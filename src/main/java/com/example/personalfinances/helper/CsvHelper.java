package com.example.personalfinances.helper;

import com.example.personalfinances.model.Category;
import com.example.personalfinances.model.Expense;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CsvHelper {
    public static String TYPE = "text/csv";
    //TODO read header from csv file
    static String[] ingHEADERs = {
            //0                 1                       2                  3    4               5
            "Data transakcji", "Data księgowania", "Dane kontrahenta", "Tytuł", "Nr rachunku", "Nazwa banku",
            // 6                7               8                                   9
            "Szczegóły", "Nr transakcji", "Kwota transakcji (waluta rachunku)", "Waluta",
            //  10                                  11          12                          13      14
            "Kwota blokady/zwolnienie blokady", "Waluta", "Kwota płatności w walucie", "Waluta", "Konto",
            // 15           16                  17
            "Bank", "Saldo po transakcji", "Waluta"
    };

    static String[] outputReportHeader = {
            //0            1                2                  3                       4               5
            "id", "transactionData", "transactionId", "transactionTitle", "transactionDescription", "transactionValue",
            // 6              7              8         9
            "bankName", "accountNumber", "comment", "category"
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

                if (record.size() < 15 || (record.get(0).isEmpty() && record.get(1).isEmpty())) {

                    return expenses;
                }
                counter++;
                if (record.get(0).length() < 8) {
                    data++;
                }
                if (record.get(2).length() < 11) {
                    description++;
                }

                String accountNumber = record.get(14).replaceAll("\"", "");
                String cost = record.get(8);
                if (record.get(8).length() < 1) {
                    cost = record.get(10);
                }

                Expense expense = Expense.builder()
                        .transactionData(record.get(data))
                        .transactionDescription(record.get(description))
                        .transactionTitle(record.get(3))
                        .transactionValue(BigDecimal
                                .valueOf(Double.parseDouble(cost.replace(',', '.'))))
                        .bankName(record.get(15))
                        .accountNumber(accountNumber)
                        .transactionId(record.get(7))
                        .build();
                expenses.add(expense);

            } else if (record.get(0).contains(ingHEADERs[0])) {
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

    //  TODO
    public void ExpenseAndCategoryToCSV(Map<Category, List<Expense>> categoryExpensesMap) throws IOException {
        String dateTimeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Charset charset = Charset.forName("windows-1250");
        System.out.println("charset.name() = " + charset.name());
        FileWriter csvFile = new FileWriter("report" + dateTimeNow + ".csv", charset);

        csvFile.append(String.join(";", outputReportHeader));
        csvFile.append("\n");

        String[] arr = new String[10];
        Set<Category> keySet = categoryExpensesMap.keySet();

        for (Category category : keySet) {
            List<Expense> expenses = categoryExpensesMap.get(category);
            for (Expense expense : expenses) {


                //-----------array variant--------------------------------
                arr[0] = expense.getId().toString();
                arr[1] = expense.getTransactionData();
                arr[2] = expense.getTransactionId();
                arr[3] = expense.getTransactionTitle();
                arr[4] = expense.getTransactionDescription();
                arr[5] = "'" + expense.getTransactionValue().toString();
                arr[6] = expense.getBankName();
                arr[7] = expense.getAccountNumber();
                arr[8] = expense.getComment();
                arr[9] = category.getName();
                csvFile.append(String.join(";", arr));
                csvFile.append("\n");
            }
        }

        csvFile.flush();
        csvFile.close();
    }

    public Map<Category, List<Expense>> csvToCategoryExpenseMap(InputStream inputStream) {
        Map<Category, List<Expense>> categoryListMap = new HashMap<>();
        Iterable<CSVRecord> records = readCSV(inputStream);
        for (CSVRecord record :
                records) {
            String id = record.get(0);

        }
        return null;
    }
}
