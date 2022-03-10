package net.millida.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class NumberUtil {

    public final ThreadLocalRandom LOCAL_RANDOM = ThreadLocalRandom.current();

    public final Pattern TIME_TO_MILLIS_PATTERN
            = Pattern.compile("(?i)" + "(\\d{1,3}(?=ns))?" + "(\\d{1,3}(?=mc))?" + "(\\d{1,3}(?=ms))?" + "(\\d{1,3}(?=s))?" + "(\\d{1,3}(?=m))?" + "(\\d{1,3}(?=h))?" + "(\\d{1,3}(?=d))?" + "(\\d{1,3}(?=w))?");


    /**
     * Добавить знак после каждой третьей цифры
     *
     * @param number - число
     * @param symbol - знак
     */
    public String spaced(int number, String symbol) {
        String integer = String.valueOf(number);
        StringBuilder builder = new StringBuilder();

        for (int a = 0; a < integer.length(); a++) {
            builder.append(integer.split("")[a]);

            if ((integer.length() - a + 2) % 3 != 0) {
                continue;
            }

            builder.append(symbol);
        }

        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    /**
     * Добавить запятую после каждой третьей цифры
     *
     * @param number - число
     */
    public String spaced(int number) {
        return spaced(number, ",");
    }

    /**
     * Возвести число в степень
     *
     * @param number - число
     * @param radian - степень
     */
    public int toRadians(int number, int radian) {
        for (int count = 0; count < radian; count++) {
            number *= number;
        }

        return number;
    }

    /**
     * Создать массив, который будет иметь в себе
     * множество значений между минимальным и
     * максимальным указанным индексом
     *
     * @param minIndex - минимальный индекс
     * @param maxIndex - максимальный индекс
     */
    public int[] toManyArray(int minIndex, int maxIndex) {
        int[] resultArray = new int[maxIndex - minIndex];

        int counter = 0;
        for (int i = minIndex; i < maxIndex; i++) {
            resultArray[counter] = i;

            counter++;
        }

        return resultArray;
    }

    /**
     * Получить рандомное число
     *
     * @param min - минимальное значение
     * @param max - максимальное значение
     */
    public int randomInt(int min, int max) {
        return LOCAL_RANDOM.nextInt(min, max);
    }

    /**
     * Получить рандомное число
     *
     * @param min - минимальное значение
     * @param max - максимальное значение
     */
    public double randomDouble(double min, double max) {
        return min + LOCAL_RANDOM.nextDouble(Math.abs(max - min));
    }

    /**
     * Преобразовать число в грамотно составленное
     * словосочетание
     *
     * @param number - число
     * @param one    - словосочетание, если число закаончивается на 1
     * @param two    - словосочетание, если число закаончивается на 2
     * @param three  - словосочетание, если число закаончивается на 5
     */
    public String formatting(int number, String one, String two, String three) {
        if (number % 100 > 10 && number % 100 < 15) {
            return number + " " + three;
        }
        switch (number % 10) {
            case 1: {
                return number + " " + one;
            }
            case 2:
            case 3:
            case 4: {
                return number + " " + two;
            }
            default: {
                return number + " " + three;
            }
        }
    }

    /**
     * Преобразовать число в грамотно составленное
     * словосочетание
     *
     * @param number - число
     * @param unit   - словосочетание
     */
    public String formatting(int number, NumberTimeUnit unit) {
        return formatting(number, unit.getOne(), unit.getTwo(), unit.getOther());
    }

    /**
     * Получить грамотно составленное время из
     * количества секунд
     *
     * @param mill - кол-во мил-сек
     */
    public String clockFormatting(long mill) {
        int seconds = (int) (mill / 1000);

        int minutes = 0, hours = 0, days = 0, weeks = 0, months = 0, years = 0;

        if (seconds >= 60) {
            int i = seconds / 60;
            seconds -= 60 * i;
            minutes += i;
        }

        if (minutes >= 60) {
            int i = minutes / 60;
            minutes -= 60 * i;
            hours += i;
        }

        if (hours >= 24) {
            int i = hours / 24;
            hours -= 24 * i;
            days += i;
        }

        if (days >= 7) {
            int i = days / 7;
            days -= 7 * i;
            weeks += i;
        }

        if (weeks >= 4) {
            int i = weeks / 4;
            weeks -= 4 * i;
            months += i;
        }

        if (months >= 12) {
            int i = months / 12;
            months -= 12 * i;
            years += i;
        }

        StringBuilder builder = new StringBuilder();

        if (years != 0) {
            builder.append(years).append(":");
        }

        if (months != 0) {
            builder.append(months).append(":");
        }

        if (weeks != 0) {
            builder.append(weeks).append(":");
        }

        if (days != 0) {
            builder.append(days).append(":");
        }

        if (hours != 0) {
            builder.append(hours).append(":");
        }

        if (minutes != 0) {
            builder.append(minutes).append(":");
        } else {
            builder.append(0).append(":");
        }

        if (seconds != 0) {
            if (seconds < 10) {
                builder.append("0").append(seconds);
            } else {
                builder.append(seconds);
            }
        } else {
            builder.append("00");
        }

        return builder.toString();
    }

    /**
     * Получить грамотно составленное время из
     * количества секунд
     *
     * @param seconds - кол-во секунд
     */
    public String getTime(int seconds) {
        int minutes = 0, hours = 0, days = 0, weeks = 0, months = 0, years = 0;

        if (seconds >= 60) {
            int i = seconds / 60;
            seconds -= 60 * i;
            minutes += i;
        }

        if (minutes >= 60) {
            int i = minutes / 60;
            minutes -= 60 * i;
            hours += i;
        }

        if (hours >= 24) {
            int i = hours / 24;
            hours -= 24 * i;
            days += i;
        }

        if (days >= 7) {
            int i = days / 7;
            days -= 7 * i;
            weeks += i;
        }

        if (weeks >= 4) {
            int i = weeks / 4;
            weeks -= 4 * i;
            months += i;
        }

        if (months >= 12) {
            int i = months / 12;
            months -= 12 * i;
            years += i;
        }

        StringBuilder builder = new StringBuilder();

        if (years != 0) {
            builder.append(formatting(years, NumberTimeUnit.YEARS)).append(" ");
        }

        if (months != 0) {
            builder.append(formatting(months, NumberTimeUnit.MONTHS)).append(" ");
        }

        if (weeks != 0) {
            builder.append(formatting(weeks, NumberTimeUnit.WEEKS)).append(" ");
        }

        if (days != 0) {
            builder.append(formatting(days, NumberTimeUnit.DAYS)).append(" ");
        }

        if (hours != 0) {
            builder.append(formatting(hours, NumberTimeUnit.HOURS)).append(" ");
        }

        if (minutes != 0) {
            builder.append(formatting(minutes, NumberTimeUnit.MINUTES)).append(" ");
        }

        if (seconds != 0) {
            builder.append(formatting(seconds, NumberTimeUnit.SECONDS));
        }

        return builder.toString();
    }

    /**
     * Получить грамотно составленное время из
     * количества миллисекунд, переведенные в секунды
     *
     * @param millis - кол-во миллисекунд
     */
    public String getTime(long millis) {
        return getTime((int) millis / 1000);
    }
    @RequiredArgsConstructor
    @Getter
    public enum NumberTimeUnit {
        SECONDS("секунда", "секунды", "секунд"),
        MINUTES("минута", "минуты", "минут"),
        HOURS("час", "часа", "часов"),
        DAYS("день", "дня", "дней"),
        WEEKS("неделя", "недели", "недель"),
        MONTHS("месяц", "месяца", "месяцев"),
        YEARS("год", "года", "лет");

        private final String one, two, other;
    }

}
