package org.mohyla.itinerary;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.Violations;
import org.springframework.modulith.docs.Documenter;

public class ModularityTests {
    ApplicationModules modules = ApplicationModules.of(ItineraryApplication.class);

    @Test
    void verifyModularity(){
        System.out.println(modules.toString());
        modules.verify();
    }
    @Test
    void renderDocumentation() throws Exception{
        var canvasOptions = Documenter.CanvasOptions.defaults();

        var docOptions = Documenter.DiagramOptions.defaults().withStyle(Documenter.DiagramOptions.DiagramStyle.UML);
        new Documenter(modules).writeDocumentation(docOptions, canvasOptions);
    }

}

