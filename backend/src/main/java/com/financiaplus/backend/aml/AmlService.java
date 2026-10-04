package com.financiaplus.backend.aml;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AmlService {

    private final List<BlacklistedPerson> blacklistedPeople = List.of(
            new BlacklistedPerson(
                    "Juan Riesgoso",
                    "1234-AML1",
                    LocalDate.of(1985, 4, 15)
            ),
            new BlacklistedPerson(
                    "María Restringida",
                    "1234-AML2",
                    LocalDate.of(1990, 8, 20)
            )
    );

    public AmlResponse searchByDocument(String document) {
        return blacklistedPeople.stream()
                .filter(person ->
                        person.identityNumber()
                                .equalsIgnoreCase(document)
                )
                .findFirst()
                .map(this::matchedResponse)
                .orElseGet(this::notMatchedResponse);
    }

    public AmlResponse searchByName(String name) {
        String searchedName = name.trim().toLowerCase();

        return blacklistedPeople.stream()
                .filter(person ->
                        person.fullName()
                                .toLowerCase()
                                .contains(searchedName)
                )
                .findFirst()
                .map(this::matchedResponse)
                .orElseGet(this::notMatchedResponse);
    }

    private AmlResponse matchedResponse(
            BlacklistedPerson person
    ) {
        return new AmlResponse(
                true,
                person.fullName(),
                person.identityNumber(),
                person.birthDate(),
                "La persona aparece en la lista negra"
        );
    }

    private AmlResponse notMatchedResponse() {
        return new AmlResponse(
                false,
                null,
                null,
                null,
                "No se encontraron coincidencias"
        );
    }

    private record BlacklistedPerson(
            String fullName,
            String identityNumber,
            LocalDate birthDate
    ) {
    }
}
