package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Analyzer {
    static BlockingQueue<String> texts1 = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> texts2 = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> texts3 = new ArrayBlockingQueue<>(100);
    static ConcurrentHashMap<Character, String> mapResult = new ConcurrentHashMap<>();


    public static void searchStringWithMaxNumberOfCharacters() throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        int quantityTexts = 10_000; //кол-во текстов

        Thread textInQueue = new Thread(() -> {
            for (int i = 0; i < quantityTexts; i++) {
                String text = generateText("abc", 100_000);
                try {
                    texts1.put(text);
                    texts2.put(text);
                    texts3.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threads.add(textInQueue);
        textInQueue.start();

        Thread characterA = new Thread(() -> {
            try {
                int counterPred = 0;
                for (int n = 0; n < quantityTexts; n++) {
                    String text = texts1.take();
                    int counter = countCharacters(text, 'a');

                    if (counter > counterPred) {
                        mapResult.put('a', text);
                        counterPred = counter;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        threads.add(characterA);
        characterA.start();

        Thread characterB = new Thread(() -> {
            try {
                int counterPred = 0;
                for (int n = 0; n < quantityTexts; n++) {
                    String text = texts2.take();
                    int counter = countCharacters(text, 'b');

                    if (counter > counterPred) {
                        mapResult.put('b', text);
                        counterPred = counter;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        threads.add(characterB);
        characterB.start();

        Thread characterC = new Thread(() -> {
            try {
                int counterPred = 0;
                for (int n = 0; n < quantityTexts; n++) {
                    String text = texts3.take();
                    int counter = countCharacters(text, 'c');

                    if (counter > counterPred) {
                        mapResult.put('c', text);
                        counterPred = counter;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        threads.add(characterC);
        characterC.start();

        for (Thread thread : threads) {
            thread.join();
        }
        printResult();
    }

    public static int countCharacters(String text, Character character) {
        int counter = 0;
        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            if (letter == character) {
                counter++;
            }
        }
        return counter;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void printResult() {
        System.out.printf("Символ 'a' содержится в тексте %d раз(-a)\n",
                countCharacters(mapResult.get('a'), 'a'));
        System.out.printf("Символ 'b' содержится в тексте %d раз(-a)\n",
                countCharacters(mapResult.get('b'), 'b'));
        System.out.printf("Символ 'c' содержится в тексте %d раз(-a)\n",
                countCharacters(mapResult.get('c'), 'c'));

//        System.out.printf("Символ 'a' содержится в тексте %d раз(-a): %s\n",
//                countCharacters(mapResult.get('a'), 'a'), mapResult.get('a'));
//        System.out.printf("Символ 'b' содержится в тексте %d раз(-a): %s\n",
//                countCharacters(mapResult.get('b'), 'b'), mapResult.get('b'));
//        System.out.printf("Символ 'c' содержится в тексте %d раз(-a): %s\n",
//                countCharacters(mapResult.get('c'), 'c'), mapResult.get('c'));
    }
}
