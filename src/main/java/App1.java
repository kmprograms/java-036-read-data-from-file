import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class App1 {

    // Metoda pomocnicza pozwoli konwertować obiekt InputStream do String.
    // InputStream reprezentuje dane z zasobu jako strumień bajtów.
    // InputStreamReader każdy bajt interpretuje jako znak.
    // Wynik tej interpretacji przechwytuje BufferedReader zastosowany w poniższej metodzie.
    // Dane z BufferedReader-a trafiają do StringBuilder, który na koniec przekształcany jest w String.
    static String convertInputStreamToString(InputStream inputStream) throws IOException {
        var sb = new StringBuilder();
        try (var br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            var line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    // Pobieranie danych z pliku podanego jako argument i zwracanie jego zawartości
    // w postaci obiektu String. Plik będzie umieszczony w katalogu resources.
    static String read1(String filename) throws IOException {
        try (var inputStream = App1.class.getResourceAsStream("/" + filename)) {
            return convertInputStreamToString(inputStream);
        }
    }

    // FileUtils pochodzi z paczki commons-io
    // Nie musisz samodzielnie stosować metody do przekształcenia InputStream do String
    /*
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.11.0</version>
        </dependency>
    */
    static String read2(String filename) throws IOException {
        var c = App1.class;
        var file = new File(c.getResource("/" + filename).getFile());
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    // Zastosowanie do odczytu BufferedReader.
    // Kiedy nie można pobrać więcej danych z pliku, wtedy readLine zwraca null
    // Wykorzystuj BufferedReader, kiedy po prostu chcesz pobierać kolejne
    // wiersze z pliku.
    static String read3(String filename) throws IOException {
        var filenamePath = "src/main/resources/" + filename;
        try (var br = new BufferedReader(new FileReader(filenamePath))) {
            return br.readLine();
        }
    }

    // Paczka NIO również dostarcza kilka ciekawych możliwości, jeżeli chodzi
    // o pobieranie danych.
    // Na początek sposób na pobieranie danych z małego pliku.
    static List<String> read4(String filename) throws IOException {
        var path = Paths.get("src/main/resources/" + filename);
        // Od razu możesz pobrać wszystkie wiersze z pliku do listy napisów
        return Files.readAllLines(path);
    }

    // Kiedy chcesz pobierać dane z dużego pliku zastosuj przykładowo BufferedReader
    static String read5(String filename) throws IOException {
        var path = Paths.get("src/main/resources/" + filename);
        try(var br = Files.newBufferedReader(path)) {
            // readLine pozwala pobrać kolejny wiersz, a nie wszystkie wiersze naraz
            return br.readLine();
        }
    }

    // Zastosowanie Streams do pobieranie danych z pliku
    // Dzięki przekształceniu kolejnych wierszy do typu strumieniowego
    // możesz wygodnie stosować operacje strumieniowe na pobranych danych.
    static List<String> read6(String filename) throws IOException {
        var path = Paths.get("src/main/resources/" + filename);
        try(var lines = Files.lines(path)) {
            return lines.map(String::toUpperCase).toList();
        }
    }

    // Możesz do odczytu zastosować Scanner, który pozwala interpretować
    // pobrane dane na wiele sposobów (jako int, double, itd.)
    // Scanner => pobieranie + parsowanie danych
    static String read7(String filename) throws IOException {
        var filePath = "src/main/resources/" + filename;
        try(var sc = new Scanner(new File(filePath))) {

            /*while (sc.hasNextLine()) {
                var line = sc.nextLine();
            }*/

            /*sc.nextInt();
            sc.nextDouble();*/

            sc.useDelimiter(" "); // Możesz decydować, według jakich separatorów
            // pobierzesz kolejne dane
            return sc.next() + " " + sc.next() + " " + sc.next();
        }
    }

    // Porównanie Scanner oraz BufferedReader.
    // BufferedReader -> synchronized, threadsafe, Scanner -> not synchronized, not threadsafe.
    // BufferedReader -> larger buffer memory 8 kB, Scanner -> smaller buffer memory 1 kB.
    // BufferedReader -> faster in execution, Scanner -> slower in execution because of parsing
    // BufferedReader -> read data, Scanner -> read + parse data.
    // BufferedReader -> reading file with long String, Scanner -> reading small user input from command prompt

    // Żeby wygodnie czytać dane binarne, możesz zastosować DataInputStream.
    // Dostarcza wygodne metody do pobierania danych w postaci bajtowej
    static String read8(String filename) throws IOException {
        var filePath = "src/main/resources/" + filename;
        try(var dis = new DataInputStream(new FileInputStream(filePath))) {

            // => Pobranie całej zawartości pliku.
            var bytesCount = dis.available();
            if (bytesCount > 0) {
                // Odczyt napisu
                var bytes = new byte[bytesCount];
                // dis.skipBytes(2);
                // dis.readNBytes(3);
                var res = dis.read(bytes); // zwraca ilość przeczytanych bajtów
                return new String(bytes);
            }
            return "";
        }
    }

    // Kiedy chcesz czytać duże pliki, zastosuj FileChannel, który jest
    // szybszy niż standardowe rozwiązania IO
    static String read9(String filename) throws IOException {
        var filePath = "src/main/resources/" + filename;
        try (
                RandomAccessFile reader = new RandomAccessFile(filePath, "r");
                FileChannel channel = reader.getChannel()) {
            var bufferSize = (int) channel.size();
            ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
            channel.read(byteBuffer);
            byteBuffer.flip(); // rozmiar bufora jest obcięty do aktualnej zawartości w buforze
            return new String(byteBuffer.array());
        }
    }

    // Odczyt danych ze wskazanego URL-a
    static String read10(String url) throws IOException {
        URL u = new URL(url);
        URLConnection urlConnection = u.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        return convertInputStreamToString(inputStream);
    }

    public static void main(String[] args) {
        try {
            var filename = "file1.txt";

            System.out.println("---------------------------------- 1 ------------------------------");
            System.out.println(read1(filename));

            System.out.println("---------------------------------- 2 ------------------------------");
            System.out.println(read2(filename));

            System.out.println("---------------------------------- 3 ------------------------------");
            System.out.println(read3(filename));

            System.out.println("---------------------------------- 4 ------------------------------");
            System.out.println(read4(filename));

            System.out.println("---------------------------------- 5 ------------------------------");
            System.out.println(read5(filename));

            System.out.println("---------------------------------- 6 ------------------------------");
            System.out.println(read6(filename));

            System.out.println("---------------------------------- 7 ------------------------------");
            System.out.println(read7(filename));

            var filename2 = "file2.txt";
            System.out.println("---------------------------------- 8 ------------------------------");
            System.out.println(read8(filename2));

            var filename3 = "file3.txt";
            System.out.println("---------------------------------- 9 ------------------------------");
            System.out.println(read9(filename3));

            System.out.println("---------------------------------- 10 -----------------------------");
            System.out.println(read10("http://www.brainjar.com/java/host/test.html"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
