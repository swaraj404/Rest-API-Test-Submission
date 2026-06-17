package pkg.swaraj.restApiTest.service;

import org.springframework.stereotype.Service;
import pkg.swaraj.restApiTest.dto.BfhlRequest;
import pkg.swaraj.restApiTest.dto.BfhlResponse;

import java.util.*;

@Service
public class BfhlServiceImpl implements BfhlService {

    private static final String VOWELS = "AEIOU";

    @Override
    public BfhlResponse processData(BfhlRequest request, String requestId) {
        long startTime = System.currentTimeMillis();

        List<String> rawData = request.getData() != null ? request.getData() : new ArrayList<>();
        int totalReceived = rawData.size();

        // Step 1: filter out null, empty, whitespace-only
        List<String> filtered = new ArrayList<>();
        for (String item : rawData) {
            if (item != null && !item.trim().isEmpty()) {
                filtered.add(item);
            }
        }
        int invalidIgnored = totalReceived - filtered.size();

        // Step 2: check duplicates, then deduplicate
        Set<String> seen = new HashSet<>();
        boolean containsDuplicates = false;
        List<String> deduped = new ArrayList<>();
        for (String item : filtered) {
            if (!seen.add(item)) {
                containsDuplicates = true;
            } else {
                deduped.add(item);
            }
        }

        // Result containers
        List<Double> allNumbers = new ArrayList<>();
        List<String> oddNumbers = new ArrayList<>();
        List<String> evenNumbers = new ArrayList<>();
        List<String> alphabets = new ArrayList<>();
        List<String> specialChars = new ArrayList<>();
        Map<String, Integer> alphabetFrequency = new TreeMap<>();
        int vowelCount = 0;
        int consonantCount = 0;
        String longestAlpha = null;
        String shortestAlpha = null;

        for (String item : deduped) {
            if (isPureNumeric(item)) {
                double val = Double.parseDouble(item);
                allNumbers.add(val);
                long rounded = Math.round(val);
                if (rounded % 2 == 0) {
                    evenNumbers.add(item);
                } else {
                    oddNumbers.add(item);
                }
            } else if (isPureAlphabetic(item)) {
                String upper = item.toUpperCase();
                alphabets.add(upper);
                for (char c : upper.toCharArray()) {
                    alphabetFrequency.merge(String.valueOf(c), 1, Integer::sum);
                    if (VOWELS.indexOf(c) >= 0) vowelCount++; else consonantCount++;
                }
                if (longestAlpha == null || upper.length() > longestAlpha.length()) longestAlpha = upper;
                if (shortestAlpha == null || upper.length() < shortestAlpha.length()) shortestAlpha = upper;
            } else if (isAlphanumeric(item)) {
                // split into digit-run and letter-run
                StringBuilder digits = new StringBuilder();
                StringBuilder letters = new StringBuilder();
                for (char c : item.toCharArray()) {
                    if (Character.isDigit(c)) digits.append(c);
                    else if (Character.isLetter(c)) letters.append(c);
                }
                if (digits.length() > 0) {
                    double val = Double.parseDouble(digits.toString());
                    allNumbers.add(val);
                    long rounded = Math.round(val);
                    if (rounded % 2 == 0) evenNumbers.add(digits.toString());
                    else oddNumbers.add(digits.toString());
                }
                if (letters.length() > 0) {
                    String upperLetters = letters.toString().toUpperCase();
                    for (char c : upperLetters.toCharArray()) {
                        alphabets.add(String.valueOf(c));
                        alphabetFrequency.merge(String.valueOf(c), 1, Integer::sum);
                        if (VOWELS.indexOf(c) >= 0) vowelCount++; else consonantCount++;
                    }
                }
            } else {
                // special characters - could be single or multiple symbols together
                specialChars.add(item);
            }
        }

        Collections.sort(allNumbers);

        double sum = 0;
        for (double n : allNumbers) sum += n;

        String largest = allNumbers.isEmpty() ? null : formatNumber(Collections.max(allNumbers));
        String smallest = allNumbers.isEmpty() ? null : formatNumber(Collections.min(allNumbers));
        String sumStr = formatNumber(sum);

        BfhlResponse response = new BfhlResponse();
        response.setIs_success(true);
        response.setRequest_id(requestId);
        response.setOdd_numbers(oddNumbers);
        response.setEven_numbers(evenNumbers);
        response.setAlphabets(alphabets);
        response.setSpecial_characters(specialChars);
        response.setSum(sumStr);
        response.setLargest_number(largest);
        response.setSmallest_number(smallest);
        response.setAlphabet_count(alphabets.size() == 0 ? 0 : countTotalLetters(alphabets));
        response.setNumber_count(allNumbers.size());
        response.setSpecial_character_count(specialChars.size());
        response.setContains_duplicates(containsDuplicates);
        response.setUnique_element_count(deduped.size());
        response.setSorted_numbers(allNumbers);
        response.setVowel_count(vowelCount);
        response.setConsonant_count(consonantCount);
        response.setLongest_alphabetic_value(longestAlpha);
        response.setShortest_alphabetic_value(shortestAlpha);
        response.setAlphabet_frequency(alphabetFrequency);
        response.setSummary(new BfhlResponse.Summary(totalReceived, deduped.size(), invalidIgnored));

        long endTime = System.currentTimeMillis();
        response.setProcessing_time_ms(endTime - startTime);

        return response;
    }

    private int countTotalLetters(List<String> alphabets) {
        int count = 0;
        for (String s : alphabets) count += s.length();
        return count;
    }

    private boolean isPureNumeric(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean isPureAlphabetic(String s) {
        return s.matches("[a-zA-Z]+");
    }

    private boolean isAlphanumeric(String s) {
        return s.matches("[a-zA-Z0-9]+") && !isPureNumeric(s) && !isPureAlphabetic(s);
    }

    private String formatNumber(double val) {
        if (val == Math.floor(val) && !Double.isInfinite(val)) {
            return String.valueOf((long) val);
        }
        return String.valueOf(val);
    }
}