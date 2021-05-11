package com.onea.sidot.passerelle;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.onea.sidot.passerelle");

        noClasses()
            .that()
            .resideInAnyPackage("com.onea.sidot.passerelle.service..")
            .or()
            .resideInAnyPackage("com.onea.sidot.passerelle.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.onea.sidot.passerelle.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
