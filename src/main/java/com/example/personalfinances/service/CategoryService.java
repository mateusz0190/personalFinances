package com.example.personalfinances.service;

import com.example.personalfinances.controller.dto.AssignKeywordsToCategoryRequestDto;
import com.example.personalfinances.controller.dto.AssignKeywordsToCategoryResponseDto;
import com.example.personalfinances.model.Category;
import com.example.personalfinances.model.Expense;
import com.example.personalfinances.model.Keyword;
import com.example.personalfinances.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CategoryService {
    private CategoryRepository categoryRepository;
    private KeywordService keywordService;


    public Category getByName(String name) {
        Category byName = categoryRepository.findByName(name);
        Category category1 = categoryRepository.findAll()
                .stream()
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .get();
        return category1;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    //  Sprawdzic czy kategoria istnieje
//     Y: sprawdzic czy keyword istnieje
//          Y: sprawdzic czy jest przypisany
//              Y: nie da rady przypisać, przejdz do nastepnego keyword
//              N: przypisać kategorie, przejdz do nastepnego keyword
//          N: Utworzyć nowy keyword, przypisać kategorię, przejdz do nastepnego keyword
//    N: wyjść i zgłośić brak kategorii
    public AssignKeywordsToCategoryResponseDto AssignKeywordToCategory(AssignKeywordsToCategoryRequestDto requestDto) {

        List<String> usedKeywords = new ArrayList<>();
        List<String> correctlyAssignedKeywords = new ArrayList<>();
        Category category = getByName(requestDto.getCategoryName());
        Set<Keyword> keywordSet = category.getKeyword();
        List<String> keywordNames = requestDto.getKeywordNames();

        for (String keywordName :
                keywordNames) {
            Keyword keyword = keywordService.getByKeywordId(keywordName);
            if (keyword.isAssigned()) {
                usedKeywords.add(keyword.getKeywordName());
                System.out.println("keyword is used: " + keywordName);
            } else {
                correctlyAssignedKeywords.add(keyword.getKeywordName());
                keyword.setAssigned(true);
                keywordSet.add(keyword);
                keywordService.save(keyword);
            }
        }
        category.setKeyword(keywordSet);
        categoryRepository.save(category);

        return AssignKeywordsToCategoryResponseDto.builder()
                .usedKeyword(usedKeywords)
                .correctlyAssignedKeyword(correctlyAssignedKeywords)
                .categoryName(category.getName())
                .build();
    }

    public Category releaseKeywordFromCategory(String categoryName, String keywordName) {
        Category category = getByName(categoryName);
        Keyword keyword = keywordService.getByKeywordId(keywordName);
        LinkedList<Keyword> keywords = category.getKeyword()
                .stream()
                .collect(Collectors.toCollection(LinkedList::new));

        if (keywords.contains(keyword)) {
            int i = keywords.indexOf(keyword);
            keywords.remove(i);
            // keywords.remove(keyword);
            keyword.setAssigned(false);
            keywordService.save(keyword);
            category.setKeyword(new HashSet<>(keywords));
            categoryRepository.save(category);
        } else
            System.out.println(keywordName + " not found...");

        return category;
    }

    public Category releaseAllKeywordsFromCategory(long id) {
        Category byId = getById(id);
        byId.setKeyword(new HashSet<>());

        return byId;
    }

    public void assignExpenseToDefaultCategory(Expense expense) {
        Set<Keyword> keywords = keywordService.getByIsAssigned(true);
        Keyword keyword = keywords
                .stream()
                .filter(
                        keyword1 -> expense.getTransactionTitle()
                                .toLowerCase()
                                .matches("(.*)" + keyword1.getKeywordName().toLowerCase() + "(.*)")
                                || expense.getTransactionDescription()
                                .toLowerCase()
                                .matches("(.*)" + keyword1.getKeywordName().toLowerCase() + "(.*)"))
                .findFirst()
                .orElse(keywordService.getByKeywordId("withoutCategory"));
        System.out.println("keyword = " + keyword.getKeywordName());
        System.out.println("expense = " + expense.getTransactionTitle()
                + " " + expense.getTransactionDescription());
        System.out.println();

        Category category1 = getByKeyword(keyword.getKeywordName()).get();
        category1.getExpenseList().add(expense);
        categoryRepository.save(category1);

    }

    public void assignUncategorizedExpensesToDefaultCategory() {
        Category category = getByName("Bez kategorii");
        Set<Keyword> keywordSet = keywordService.getByIsAssigned(true);
        List<Expense> expenseList = category.getExpenseList().stream().collect(Collectors.toList());
        expenseList.forEach(expense -> assignExpenseToDefaultCategory(expense));
    }


    public Category getById(long id) {
        return categoryRepository.findById(id).orElse(new Category());
    }

    public Optional<Category> getByKeyword(String kName) {
        List<Category> all = getAll();
        Keyword keyword = keywordService.getByKeywordId(kName);
        Optional<Category> optionalCategory = all.stream()
                .filter(category -> category.getKeyword().contains(keyword))
                .findFirst();
        return optionalCategory;
    }



    public boolean CategoryisPresentByID(long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryOptional.isPresent();
    }

    public Category removeById(long id) {
        Category category = categoryRepository.findById(id).get();
        if (category.getKeyword().size() > 0) {
            category = releaseAllKeywordsFromCategory(id);
        }
        category.setExpenseList(new ArrayList<>());
        categoryRepository.deleteById(id);
        return category;
    }
//      TO do:
    //
//    public void getMonthlyCategoryBilance(String date) {
//        List<Category> categoriesWithExpenses = getAll()
//                .stream()
//                .filter(category -> category.getExpenseList().size() > 0)
//                .toList();
//        for (Category category :
//                categoriesWithExpenses) {
//            BigDecimal income = new BigDecimal(0);
//            AtomicReference<BigDecimal> outcome = new AtomicReference<>(income);
//            Stream<Expense> stream = category.getExpenseList().stream();
//            stream.takeWhile(expense -> Boolean.valueOf(expense.getTransactionValue().signum() != 0))
//                    .forEach(expense -> income = income.add(expense.getTransactionValue()));
//
//            stream.takeWhile(expense -> !Boolean.valueOf(expense.getTransactionValue().signum() != 0))
//                    .forEach(expense -> outcome = outcome.get().add(expense.getTransactionValue()));
//
//            BigDecimal bilance = outcome.get().add(income);
//        }
//    }

    private LocalDate parseToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (date.length() <= 6) {
            formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            date = date + "01";
        }
        LocalDate parse = LocalDate.parse(date, formatter);
        return parse;
    }
}
