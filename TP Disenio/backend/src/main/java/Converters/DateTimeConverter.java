package Converters;

import net.bytebuddy.asm.Advice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class DateTimeConverter {
        private static final String[] PATTERNS = {
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm",
                "yyyy-MM-dd'T'HH:mm:ss.SSS"
        };
        public static LocalDateTime convert(String dateTimeString) {
            for (String pattern : PATTERNS) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    return LocalDateTime.parse(dateTimeString, formatter);
                } catch (DateTimeParseException e) {
                    throw new RuntimeException(e);
                }
            }


            return null;
        }

        public static LocalDateTime convertSinTiempo(String fecha){
            return DateTimeConverter.convert(fecha.concat("T00:00:00"));
        }
        public static void main(String[] args){
                String a = "2024-11-14";
                LocalDateTime b = DateTimeConverter.convertSinTiempo(a);

                System.out.println(b);
        }
}
