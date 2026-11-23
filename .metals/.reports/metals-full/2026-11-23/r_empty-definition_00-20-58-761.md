error id: file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/controller/DashboardController.java:_empty_/DashboardService#
file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/controller/DashboardController.java
empty definition using pc, found symbol in pc: _empty_/DashboardService#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 478
uri: file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/controller/DashboardController.java
text:
```scala
package com.botica.lisette.controller;

import com.botica.lisette.service.ConfiguracionService;
import com.botica.lisette.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardSe@@rvice dashboardService;
    private final ConfiguracionService configuracionService;

    public DashboardController(DashboardService dashboardService, ConfiguracionService configuracionService) {
        this.dashboardService = dashboardService;
        this.configuracionService = configuracionService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("r", dashboardService.resumen());
        model.addAttribute("diasAviso", configuracionService.diasAvisoVencimiento());
        return "dashboard";
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/DashboardService#