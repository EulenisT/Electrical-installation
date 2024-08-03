package model;

import java.time.LocalDate;

public record RInstallation(Integer id, LocalDate date, String adresse, Integer cp, String commune) {
}
